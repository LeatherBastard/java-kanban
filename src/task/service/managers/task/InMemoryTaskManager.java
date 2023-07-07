package task.service.managers.task;

import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.history.HistoryManager;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, SimpleTask> simpleTasks;
    protected final HashMap<Integer, Epic> epicTasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final List<Task> tasksWithoutDate;
    protected final Set<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        tasksWithoutDate = new ArrayList<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public void addTaskToPrioritized(Task task) {
        if (task.getStartTime() == null) {
            tasksWithoutDate.add(task);
        } else {
            prioritizedTasks.add(task);
        }
    }

    public void removeTaskFromPrioritized(Task task) {
        if (task.getStartTime() == null) {
            tasksWithoutDate.remove(task);
        } else {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        addTaskToPrioritized(simpleTask);
        simpleTask.setId(simpleTask.hashCode());
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        addTaskToPrioritized(subtask);
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        if (epic != null && !epic.getSubtasks().contains(subtask)) {
            epic.addSubtask(subtask);
        }
        subtask.setId(subtask.hashCode());
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void addEpicTask(Epic epic) {
        addTaskToPrioritized(epic);
        epic.setId(epic.hashCode());
        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicTasks.put(epic.getId(), epic);
        for (Subtask subtask : epicSubtasks) {
            subtask.setEpicOwnerId(epic.getId());
            addSubtask(subtask);
            addTaskToPrioritized(subtask);
        }
    }


    @Override
    public SimpleTask getSimpleTaskById(int id) {
        SimpleTask simpleTask = simpleTasks.get(id);
        historyManager.add(simpleTask);
        return simpleTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        ArrayList<Task> result = new ArrayList<>(prioritizedTasks);
        result.addAll(tasksWithoutDate);
        return result;
    }

    @Override
    public void removeSimpleTaskById(int id) {
        removeTaskFromPrioritized(getSimpleTaskById(id));
        simpleTasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeSubtaskById(int id) {
        removeTaskFromPrioritized(getSubtaskById(id));
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        if (epic != null) {
            epic.removeSubtask(subtask.getId());
        }
        subtasks.remove(id);
    }

    @Override
    public void removeEpicTaskById(int id) {
        removeTaskFromPrioritized(getEpicTaskById(id));
        Epic epic = epicTasks.get(id);
        List<Subtask> epicSubtasks = epic.getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            removeTaskFromPrioritized(getSubtaskById(id));
            subtasks.remove(subtask.getId());
        }
        epicTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllSimpleTasks() {
        simpleTasks.values().forEach(this::removeTaskFromPrioritized);
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(this::removeTaskFromPrioritized);
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
            epic.calculateTime();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasks.values().forEach(this::removeTaskFromPrioritized);
        epicTasks.clear();
        removeAllSubtasks();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        int simpleTaskId = simpleTask.getId();
        if (simpleTasks.containsKey(simpleTaskId)) {
            SimpleTask previousTask = getSimpleTaskById(simpleTaskId);
            removeTaskFromPrioritized(previousTask);
            if (isDateIntersected(simpleTask)) {
                addTaskToPrioritized(previousTask);
            } else {
                simpleTasks.put(simpleTask.getId(), simpleTask);
                addTaskToPrioritized(simpleTask);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask previousSubtask = getSubtaskById(subtaskId);
            removeTaskFromPrioritized(previousSubtask);
            if (isDateIntersected(previousSubtask)) {
                addTaskToPrioritized(previousSubtask);
            } else {
                Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
                epic.removeSubtask(subtask.getId());
                epic.addSubtask(subtask);
                subtasks.put(subtask.getId(), subtask);
                addTaskToPrioritized(subtask);
            }
        }
    }

    protected boolean isDateIntersected(Task task) {
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime prioritizedTaskStartTime = prioritizedTask.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();
            LocalDateTime prioritizedTaskEndTIme = prioritizedTask.getEndTime();
            boolean isIntersectionFound = taskStartTime.equals(prioritizedTaskStartTime) ||
                    taskEndTime.equals(prioritizedTaskEndTIme);
            if (isIntersectionFound) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateEpicTask(Epic epic) {
        int epicTaskId = epic.getId();
        if (epicTasks.containsKey(epicTaskId)) {
            Epic previousEpic = getEpicTaskById(epicTaskId);
            removeTaskFromPrioritized(previousEpic);
            if (isDateIntersected(epic)) {
                addTaskToPrioritized(epic);
            } else {
                List<Subtask> oldEpicSubtasks = getEpicTaskById(epic.getId()).getSubtasks();
                List<Subtask> newEpicSubtasks = epic.getSubtasks();
                if (!oldEpicSubtasks.equals(newEpicSubtasks)) {
                    for (Subtask subtask : oldEpicSubtasks) {
                        removeTaskFromPrioritized(
                                subtasks.remove(subtask.getId())
                        );
                    }
                    for (Subtask subtask : newEpicSubtasks) {
                        addTaskToPrioritized(
                                subtasks.put(subtask.getId(), subtask)
                        );
                    }
                }
                epicTasks.put(epic.getId(), epic);
                addTaskToPrioritized(epic);
            }
        }
    }
}
