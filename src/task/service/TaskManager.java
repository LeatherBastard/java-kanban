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

    public List<SimpleTask> getAllSimpleTasks() {
        ArrayList<SimpleTask> tasksList = new ArrayList<>();
        for (int taskId : simpleTasks.keySet()) {
            tasksList.add(simpleTasks.get(taskId));
        }
        return tasksList;
    }

    public List<Subtask> getAllSubtasks() {
        ArrayList<Subtask> tasksList = new ArrayList<>();
        for (int taskId : subtasks.keySet()) {
            tasksList.add(subtasks.get(taskId));
        }
        return tasksList;
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
        ArrayList<Epic> tasksList = new ArrayList<>();
        for (int taskId : epicTasks.keySet()) {
            tasksList.add(epicTasks.get(taskId));
        }
        return tasksList;
    }

    public void removeSimpleTaskById(int id) {
        simpleTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void removeEpicTasksById(int id) {
        epicTasks.remove(id);
    }

    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpicTasks() {
        epicTasks.clear();
    }

    public void updateSimpleTask(SimpleTask simpleTask) {
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpicTask(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

}
