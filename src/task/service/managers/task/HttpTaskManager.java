package task.service.managers.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.client.KVTaskClient;
import task.service.managers.server.KVServer;

import java.io.IOException;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager implements TaskManager {
    private static final String simpleTasksKey = "simpleTasks";
    private static final String subtasksKey = "subtasks";
    private static final String epicTasksKey = "epicTasks";
    private static final String historyTasksKey = "historyTasks";

    private KVTaskClient client;
    private Gson gson;


    public HttpTaskManager(String serverUrl) {
        super(null);
        client = new KVTaskClient(serverUrl);
        gson = Managers.getGson();
        loadState();

    }

    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        super.addSimpleTask(simpleTask);

    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);

    }

    @Override
    public void addEpicTask(Epic epic) {
        super.addEpicTask(epic);

    }

    @Override
    public SimpleTask getSimpleTaskById(int id) {

        return super.getSimpleTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {

        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicTaskById(int id) {

        return super.getEpicTaskById(id);
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return super.getAllSimpleTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {

        return super.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        return super.getAllEpicTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public SimpleTask removeSimpleTaskById(int id) {
        SimpleTask simpleTask = super.removeSimpleTaskById(id);
        return simpleTask;
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        Subtask subtask = super.removeSubtaskById(id);
        return subtask;
    }

    @Override
    public Epic removeEpicTaskById(int id) {
        Epic epic = super.removeEpicTaskById(id);
        return epic;
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        super.updateSimpleTask(simpleTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
    }

    @Override
    protected void loadState() {
        JsonArray jsonSimpleTasksArray = gson.fromJson(client.load(simpleTasksKey), JsonArray.class);
        if (jsonSimpleTasksArray != null)
            jsonSimpleTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, SimpleTask.class))
                    .forEach(simpleTask -> simpleTasks.put(simpleTask.getId(), simpleTask));
        JsonArray jsonEpicTasksTasksArray = gson.fromJson(client.load(epicTasksKey), JsonArray.class);
        if (jsonEpicTasksTasksArray != null)
            jsonEpicTasksTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, Epic.class))
                    .forEach(epic -> epicTasks.put(epic.getId(), epic));
        JsonArray jsonSubtasksTasksArray = gson.fromJson(client.load(subtasksKey), JsonArray.class);
        if (jsonSubtasksTasksArray != null)
            jsonSubtasksTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, Subtask.class))
                    .forEach(this::addSubtask);
        JsonArray jsonHistoryArray = gson.fromJson(client.load(historyTasksKey), JsonArray.class);
        if (jsonHistoryArray != null)
            jsonHistoryArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, Task.class))
                    .forEach(historyManager::add);
    }

    @Override
    protected void saveState() throws ManagerSaveException {
        client.put(simpleTasksKey, gson.toJson(simpleTasks.values()));
        client.put(epicTasksKey, gson.toJson(epicTasks.values()));
        client.put(subtasksKey, gson.toJson(subtasks.values()));
        client.put(historyTasksKey, gson.toJson(historyManager.getHistory()));
    }

}
