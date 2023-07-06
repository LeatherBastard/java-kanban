package task.service.managers.task;

import com.sun.source.tree.Tree;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.history.HistoryManager;

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
        prioritizedTasks = new TreeSet<>((Task t1, Task t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
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
        simpleTask.setId(simpleTask.hashCode());
        simpleTasks.put(simpleTask.getId(), simpleTask);
        addTaskToPrioritized(simpleTask);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        if (epic != null && !epic.getSubtasks().contains(subtask)) {
            epic.addSubtask(subtask);
        }
        subtask.setId(subtask.hashCode());
        subtasks.put(subtask.getId(), subtask);
        addTaskToPrioritized(subtask);
    }

    @Override
    public void addEpicTask(Epic epic) {
        epic.setId(epic.hashCode());
        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicTasks.put(epic.getId(), epic);
        addTaskToPrioritized(epic);
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
        simpleTasks.values().stream().forEach(task -> removeTaskFromPrioritized(task));
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().stream().forEach(task -> removeTaskFromPrioritized(task));
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
            epic.calculateTime();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasks.values().stream().forEach(task -> removeTaskFromPrioritized(task));
        epicTasks.clear();
        removeAllSubtasks();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        if (simpleTasks.containsKey(simpleTask.getId())) {
            removeTaskFromPrioritized(getSimpleTaskById(simpleTask.getId()));
            simpleTasks.put(simpleTask.getId(), simpleTask);
            addTaskToPrioritized(simpleTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
            epic.removeSubtask(subtask.getId());
            epic.addSubtask(subtask);
            removeTaskFromPrioritized(getSubtaskById(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            addTaskToPrioritized(subtask);
        }
    }

    @Override
    public void updateEpicTask(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
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
            removeTaskFromPrioritized(getEpicTaskById(epic.getId()));
            epicTasks.put(epic.getId(), epic);
            addTaskToPrioritized(epic);
        }
    }
}
