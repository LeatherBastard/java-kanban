package task.service.managers.task;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.client.KVTaskClient;

import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager implements TaskManager {
    private static final String SIMPLE_TASKS_KEY = "simpleTasks";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String EPIC_TASKS_KEY = "epicTasks";
    private static final String HISTORY_TASKS_KEY = "historyTasks";

    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String serverUrl) {
        super(null);
        client = new KVTaskClient(serverUrl);
        gson = Managers.getGson();
        loadState();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    protected void loadState() {
        String historyString = client.load(HISTORY_TASKS_KEY);
        JsonArray jsonSimpleTasksArray = gson.fromJson(client.load(SIMPLE_TASKS_KEY), JsonArray.class);
        if (jsonSimpleTasksArray != null && jsonSimpleTasksArray.size() != 0)
            jsonSimpleTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, SimpleTask.class))
                    .forEach(simpleTask -> simpleTasks.put(simpleTask.getId(), simpleTask));
        JsonArray jsonEpicTasksTasksArray = gson.fromJson(client.load(EPIC_TASKS_KEY), JsonArray.class);
        if (jsonEpicTasksTasksArray != null && jsonEpicTasksTasksArray.size() != 0)
            jsonEpicTasksTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, Epic.class))
                    .forEach(epic -> epicTasks.put(epic.getId(), epic));
        JsonArray jsonSubtasksTasksArray = gson.fromJson(client.load(SUBTASKS_KEY), JsonArray.class);
        if (jsonSubtasksTasksArray != null && jsonSubtasksTasksArray.size() != 0)
            jsonSubtasksTasksArray.asList().stream()
                    .map(jsonElement -> gson.fromJson(jsonElement, Subtask.class))
                    .forEach(this::addSubtask);
        if (!historyString.isEmpty()) {
            List<Integer> history = historyFromString(historyString);
            for (Integer key : history) {
                Task task = simpleTasks.get(key);
                if (task == null) {
                    task = epicTasks.get(key);
                    if (task == null) {
                        task = subtasks.get(key);
                    }
                }
                historyManager.add(task);
            }
        }
    }

    @Override
    protected void saveState() throws ManagerSaveException {
        client.put(HISTORY_TASKS_KEY, gson.toJson(FileBackedTaskManager.historyToString(historyManager)));
        client.put(SIMPLE_TASKS_KEY, gson.toJson(simpleTasks.values()));
        client.put(EPIC_TASKS_KEY, gson.toJson(epicTasks.values()));
        client.put(SUBTASKS_KEY, gson.toJson(subtasks.values()));
    }

}
