package task.service;

import task.model.Epic;
import task.model.Subtask;
import task.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;
    private HashMap<Integer, Task> tasks;

    public TaskManager() {
        tasks = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(id, task);
        id++;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (int taskId : tasks.keySet()) {
            tasksList.add(tasks.get(taskId));
        }
        return tasksList;
    }

    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        if (tasks.containsValue(epic)) {
            for (int taskId : tasks.keySet()) {
                if (tasks.get(taskId).equals(epic)) {
                    return epic.getSubtasks();
                }
            }
        }
        return new ArrayList<>();
    }

    public void removeById(int id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks.clear();
        id = 0;
    }

    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

}
