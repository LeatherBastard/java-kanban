package task.service;

import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, SimpleTask> simpleTasks;
    private HashMap<Integer, Epic> epicTasks;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addSimpleTask(SimpleTask simpleTask) {
        simpleTask.setId(simpleTask.hashCode());
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(subtask.hashCode());
        subtasks.put(subtask.getId(), subtask);
    }

    public void addEpicTask(Epic epic) {
        epic.setId(epic.hashCode());
        epicTasks.put(epic.getId(), epic);
    }

    public SimpleTask getSimpleTaskById(int id) {
        return simpleTasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    private Epic getEpicTaskByName(String name) {
        for (Epic epic : epicTasks.values()) {
            if (epic.getName().equals(name)) {
                return epic;
            }
        }
        return null;
    }

    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        String epicName = epic.getName();
        for (Subtask subtask : subtasks.values()) {
            if (epicName.equals(subtask.getEpicName())) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public void removeSimpleTaskById(int id) {
        simpleTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        String epicName = subtask.getEpicName();
        Epic epic = getEpicTaskByName(epicName);
        epic.removeSubtask(subtask.getName());
        subtasks.remove(id);
    }

    public void removeEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        List<Subtask> epicSubtasks = epic.getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epicTasks.remove(id);
    }

    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
        }
    }

    public void removeAllEpicTasks() {
        epicTasks.clear();
        removeAllSubtasks();
    }

    public void updateSimpleTask(SimpleTask simpleTask) {
        if (simpleTasks.containsKey(simpleTask.getId()))
            simpleTasks.put(simpleTask.getId(), simpleTask);
        else
            addSimpleTask(simpleTask);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            String oldSubtaskEpicName = oldSubtask.getEpicName();
            if (!oldSubtaskEpicName.equals(subtask.getEpicName())) {
                Epic oldEpic = getEpicTaskByName(oldSubtaskEpicName);
                oldEpic.removeSubtask(oldSubtask.getName());
            } else {
                Epic newEpic = getEpicTaskByName(subtask.getName());
                newEpic.addSubtask(subtask);
            }
            subtasks.put(subtask.getId(), subtask);
        } else {
            addSubtask(subtask);
        }

    }

    public void updateEpicTask(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            Epic oldEpic = getEpicTaskById(epic.getId());
            if (!epic.getName().equals(oldEpic.getName())) {
                removeEpicTaskById(epic.getId());
            }
            epicTasks.put(epic.getId(), epic);
        } else {
            addEpicTask(epic);
        }

    }
}
