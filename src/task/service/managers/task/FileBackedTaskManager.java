package task.service.managers.task;

import task.model.*;
import task.service.managers.history.HistoryManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final String NEW_LINE_CHARACTER = "\n";
    private static final String CSV_FILE_HEADER = "id,type,name,status,description,epic" + NEW_LINE_CHARACTER;
    private static final int CSV_FILE_DATA_START = 1;
    private static final String TASKS_PATH = "tasks.csv";


    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        super.addSimpleTask(simpleTask);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpicTask(Epic epic) {
        super.addEpicTask(epic);
        save();
    }

    public Task fromString(String string) {
        Task result;
        String[] taskInfo = string.split(",");
        switch (TaskType.valueOf(taskInfo[1])) {
            case TASK:
                SimpleTask simpleTask = new SimpleTask(taskInfo[2], taskInfo[4]);
                simpleTask.setId(Integer.parseInt(taskInfo[0]));
                simpleTask.setStatus(TaskStatus.valueOf(taskInfo[3]));
                result = simpleTask;
                break;
            case EPIC:
                Epic epic = new Epic(taskInfo[2], taskInfo[4]);
                epic.setId(Integer.parseInt(taskInfo[0]));
                result = epic;
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(taskInfo[2], taskInfo[4]);
                subtask.setId(Integer.parseInt(taskInfo[0]));
                subtask.setStatus(TaskStatus.valueOf(taskInfo[3]));
                subtask.setEpicOwnerId(Integer.parseInt(taskInfo[5]));
                result = subtask;
                break;
            default:
                result = new SimpleTask("", "");
        }
        return result;
    }

    @Override
    public SimpleTask getSimpleTaskById(int id) {
        save();
        return super.getSimpleTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicTaskById(int id) {
        save();
        return super.getEpicTaskById(id);
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        save();
        return super.getAllSimpleTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        save();
        return super.getAllSubtasks();
    }

    @Override
    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        save();
        return super.getAllSubtasksForEpic(epic);
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        save();
        return super.getAllEpicTasks();
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        String[] historyValues = value.split(",");
        for (String element : historyValues) {
            result.add(Integer.parseInt(element));
        }
        return result;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder result = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            result.append(history.get(i).getId());
            if (i != history.size() - 1)
                result.append(",");
        }
        return result.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager();
        List<String> fileData;

        fileData = Files.readAllLines(file.toPath());
        if (fileData.size() > CSV_FILE_DATA_START) {
            String data;
            int i;
            for (i = CSV_FILE_DATA_START, data = fileData.get(i); !data.isEmpty(); i++, data = fileData.get(i)) {
                String[] taskInfo = data.split(",");
                Task task = manager.fromString(data);
                switch (TaskType.valueOf(taskInfo[1])) {
                    case TASK:
                        SimpleTask simpleTask = (SimpleTask) task;
                        manager.simpleTasks.put(simpleTask.getId(), simpleTask);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epicTasks.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.addSubtask(subtask);
                        break;
                }
            }
            i++;
            if (i < fileData.size()) {
                List<Integer> history = historyFromString(fileData.get(i));
                Map<Integer, Task> allTasks = new HashMap<>();
                allTasks.putAll(manager.simpleTasks);
                allTasks.putAll(manager.epicTasks);
                allTasks.putAll(manager.subtasks);
                for (Integer key : history) {
                    Task task;
                    task = allTasks.get(key);
                    if (task != null) {
                        manager.historyManager.add(task);
                    }
                }
            }
        }

        return manager;
    }

    @Override
    public void removeSimpleTaskById(int id) {
        super.removeSimpleTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicTaskById(int id) {
        super.removeEpicTaskById(id);
        save();
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_PATH))) {
            writer.write(CSV_FILE_HEADER);
            for (SimpleTask task : simpleTasks.values()) {
                writer.write(task.toString() + NEW_LINE_CHARACTER);
            }
            for (Epic epic : epicTasks.values()) {
                writer.write(epic.toString() + NEW_LINE_CHARACTER);
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(subtask.toString() + NEW_LINE_CHARACTER);
            }
            writer.write(NEW_LINE_CHARACTER);
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        super.updateSimpleTask(simpleTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        save();
    }

}
