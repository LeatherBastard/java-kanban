package task.service.managers.task;

import task.model.*;
import task.service.managers.history.HistoryManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static task.model.Task.formatter;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final String NEW_LINE_CHARACTER = System.lineSeparator();
    private static final String CSV_FILE_HEADER = "id,type,name,status,description,duration,startTime,epic" + NEW_LINE_CHARACTER;
    private static final int CSV_FILE_DATA_START = 1;
    public static final String TASKS_PATH = "tasks.csv";
    private String path;


    public FileBackedTaskManager(String path) {
        try {
            this.path = path;
            if (path != null)
                loadState();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        super.addSimpleTask(simpleTask);
        saveState();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        saveState();
    }

    @Override
    public void addEpicTask(Epic epic) {
        super.addEpicTask(epic);
        saveState();
    }

    private Task fromString(String string) {
        Task result;
        String[] taskInfo = string.split(",");
        switch (TaskType.valueOf(taskInfo[1])) {
            case TASK:
                SimpleTask simpleTask = new SimpleTask(taskInfo[2], taskInfo[4]);
                simpleTask.setId(Integer.parseInt(taskInfo[0]));
                simpleTask.setStatus(TaskStatus.valueOf(taskInfo[3]));
                simpleTask.setDuration(Duration.ofMinutes(Integer.parseInt(taskInfo[5])));
                simpleTask.setStartTime(LocalDateTime.parse(taskInfo[6], formatter));
                result = simpleTask;
                break;
            case EPIC:
                Epic epic = new Epic(taskInfo[2], taskInfo[4]);
                epic.setId(Integer.parseInt(taskInfo[0]));
                epic.calculateTime();
                result = epic;
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(taskInfo[2], taskInfo[4]);
                subtask.setId(Integer.parseInt(taskInfo[0]));
                subtask.setStatus(TaskStatus.valueOf(taskInfo[3]));
                subtask.setDuration(Duration.ofMinutes(Integer.parseInt(taskInfo[5])));
                subtask.setStartTime(LocalDateTime.parse(taskInfo[6], formatter));
                subtask.setEpicOwnerId(Integer.parseInt(taskInfo[7]));
                result = subtask;
                break;
            default:
                result = new SimpleTask("", "");
        }
        return result;
    }

    @Override
    public SimpleTask getSimpleTaskById(int id) {
        SimpleTask simpleTask = super.getSimpleTaskById(id);
        saveState();
        return simpleTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        saveState();
        return subtask;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epic = super.getEpicTaskById(id);
        saveState();
        return epic;
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        saveState();
        return super.getAllSimpleTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        saveState();
        return super.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        saveState();
        return super.getAllEpicTasks();
    }

    @Override
    public List<Task> getHistory() {
        saveState();
        return super.getHistory();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        String[] historyValues = value.split(",");
        for (String element : historyValues) {
            element.replaceAll("\"", "");
            try {
                result.add(Integer.parseInt(element));
            } catch (NumberFormatException e) {
                e.getMessage();
            }
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

    protected void loadState() throws IOException {
        List<String> fileData;
        fileData = Files.readAllLines(Path.of(path));
        if (fileData.size() > CSV_FILE_DATA_START) {
            String data;
            int i;
            for (i = CSV_FILE_DATA_START, data = fileData.get(i); !data.isEmpty(); i++, data = fileData.get(i)) {
                String[] taskInfo = data.split(",");
                Task task = fromString(data);
                switch (TaskType.valueOf(taskInfo[1])) {
                    case TASK:
                        SimpleTask simpleTask = (SimpleTask) task;
                        simpleTasks.put(simpleTask.getId(), simpleTask);
                        prioritizedTasks.add(simpleTask);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        epicTasks.put(epic.getId(), epic);
                        prioritizedTasks.add(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        addSubtask(subtask);
                        break;
                }
            }
            i++;
            if (i < fileData.size()) {
                List<Integer> history = historyFromString(fileData.get(i));
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
    }

    @Override
    public SimpleTask removeSimpleTaskById(int id) {
        SimpleTask simpleTask = super.removeSimpleTaskById(id);
        saveState();
        return simpleTask;
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        Subtask subtask = super.removeSubtaskById(id);
        saveState();
        return subtask;
    }

    @Override
    public Epic removeEpicTaskById(int id) {
        Epic epic = super.removeEpicTaskById(id);
        saveState();
        return epic;
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
        saveState();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        saveState();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        saveState();
    }


    protected void saveState() throws ManagerSaveException {
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
        saveState();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        saveState();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        saveState();
    }

}
