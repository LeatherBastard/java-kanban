package task.service.managers.task;

import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, SimpleTask> simpleTasks;
    protected final HashMap<Integer, Epic> epicTasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        simpleTask.setId(simpleTask.hashCode());
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        if (!epic.getSubtasks().contains(subtask)) {
            subtask.setId(subtask.hashCode());
            epic.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void addEpicTask(Epic epic) {
        epic.setId(epic.hashCode());
        List<Subtask> epicSubtasks = epic.getSubtasks();
        epicTasks.put(epic.getId(), epic);
        for (Subtask subtask : epicSubtasks) {
            subtask.setEpicOwnerId(epic.getId());
            addSubtask(subtask);
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

    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        int epicOwnerId = epic.getId();
        for (Subtask subtask : subtasks.values()) {
            if (epicOwnerId == subtask.getEpicOwnerId()) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
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
    public void removeSimpleTaskById(int id) {
        simpleTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
        epic.removeSubtask(subtask.getId());
        subtasks.remove(id);
    }

    @Override
    public void removeEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        List<Subtask> epicSubtasks = epic.getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epicTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasks.clear();
        removeAllSubtasks();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        if (simpleTasks.containsKey(simpleTask.getId()))
            simpleTasks.put(simpleTask.getId(), simpleTask);
        else
            addSimpleTask(simpleTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = getEpicTaskById(subtask.getEpicOwnerId());
            epic.removeSubtask(subtask.getId());
            epic.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
        } else {
            addSubtask(subtask);
        }
    }

    @Override
    public void updateEpicTask(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            List<Subtask> oldEpicSubtasks = getEpicTaskById(epic.getId()).getSubtasks();
            List<Subtask> newEpicSubtasks = epic.getSubtasks();
            if (!oldEpicSubtasks.equals(newEpicSubtasks)) {
                for (Subtask subtask : oldEpicSubtasks) {
                    subtasks.remove(subtask.getId());
                }
                for (Subtask subtask : newEpicSubtasks) {
                    subtasks.put(subtask.getId(), subtask);
                }
            }
            epicTasks.put(epic.getId(), epic);
        } else {
            addEpicTask(epic);
        }
    }
}
