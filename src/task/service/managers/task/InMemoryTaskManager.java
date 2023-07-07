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
    protected final Set<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((Task t1, Task t2) ->
        {
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return 0;
            } else if (t1.getStartTime() == null) {
                return 1;
            } else if (t2.getStartTime() == null) {
                return -1;
            } else {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
    }


    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        if (!isDateIntersected(simpleTask)) {
            prioritizedTasks.add(simpleTask);
            simpleTask.setId(simpleTask.hashCode());
            simpleTasks.put(simpleTask.getId(), simpleTask);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!isDateIntersected(subtask)) {
            prioritizedTasks.add(subtask);
            Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
            if (epic != null && !epic.getSubtasks().contains(subtask)) {
                epic.addSubtask(subtask);
            }
            subtask.setId(subtask.hashCode());
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void addEpicTask(Epic epic) {
        if (!isDateIntersected(epic)) {
            prioritizedTasks.add(epic);
            epic.setId(epic.hashCode());
            List<Subtask> epicSubtasks = epic.getSubtasks();
            epicTasks.put(epic.getId(), epic);
            for (Subtask subtask : epicSubtasks) {
                subtask.setEpicOwnerId(epic.getId());
                //addSubtask(subtask);
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
            }
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
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void removeSimpleTaskById(int id) {
        prioritizedTasks.remove(getSimpleTaskById(id));
        simpleTasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeSubtaskById(int id) {
        prioritizedTasks.remove(getSubtaskById(id));
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        if (epic != null) {
            epic.removeSubtask(subtask.getId());
        }
        subtasks.remove(id);
    }

    @Override
    public void removeEpicTaskById(int id) {
        prioritizedTasks.remove(getEpicTaskById(id));
        Epic epic = epicTasks.get(id);
        List<Subtask> epicSubtasks = epic.getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtask.getId());
        }
        epicTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllSimpleTasks() {
        simpleTasks.values().forEach(prioritizedTasks::remove);
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
            epic.calculateTime();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasks.values().forEach(prioritizedTasks::remove);
        epicTasks.clear();
        removeAllSubtasks();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        int simpleTaskId = simpleTask.getId();
        if (simpleTasks.containsKey(simpleTaskId)) {
            SimpleTask previousTask = getSimpleTaskById(simpleTaskId);
            prioritizedTasks.remove(previousTask);
            if (isDateIntersected(simpleTask)) {
                prioritizedTasks.add(previousTask);
            } else {
                simpleTasks.put(simpleTask.getId(), simpleTask);
                prioritizedTasks.add(simpleTask);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask previousSubtask = getSubtaskById(subtaskId);
            prioritizedTasks.remove(previousSubtask);
            if (isDateIntersected(previousSubtask)) {
                prioritizedTasks.add(previousSubtask);
            } else {
                Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
                epic.removeSubtask(subtask.getId());
                epic.addSubtask(subtask);
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
            }
        }
    }

    protected boolean isDateIntersected(Task task) {
        if (task.getStartTime() != null) {
            for (Task prioritizedTask : prioritizedTasks) {
                if (prioritizedTask.getStartTime() != null) {
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
            }
        }
        return false;
    }

    @Override
    public void updateEpicTask(Epic epic) {
        int epicTaskId = epic.getId();
        if (epicTasks.containsKey(epicTaskId)) {
            Epic previousEpic = getEpicTaskById(epicTaskId);
            prioritizedTasks.remove(previousEpic);
            if (isDateIntersected(epic)) {
                prioritizedTasks.add(epic);
            } else {
                List<Subtask> oldEpicSubtasks = getEpicTaskById(epic.getId()).getSubtasks();
                List<Subtask> newEpicSubtasks = epic.getSubtasks();
                if (!oldEpicSubtasks.equals(newEpicSubtasks)) {
                    for (Subtask subtask : oldEpicSubtasks) {
                        prioritizedTasks.remove(
                                subtasks.remove(subtask.getId())
                        );
                    }
                    for (Subtask subtask : newEpicSubtasks) {
                        prioritizedTasks.add(
                                subtasks.put(subtask.getId(), subtask)
                        );
                    }
                }
                epicTasks.put(epic.getId(), epic);
                prioritizedTasks.add(epic);
            }
        }
    }
}
