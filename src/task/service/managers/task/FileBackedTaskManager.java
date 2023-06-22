package task.service.managers.task;

import task.model.*;
import task.service.managers.history.HistoryManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final String NULL_VALUE_MESSAGE = "Path has null value!";
    private static final String TASKS_PATH = "tasks.csv";

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

    public static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        String[] historyValues = value.split(",");
        for (String element : historyValues) {
            result.add(Integer.parseInt(element));
        }
        return result;
    }


    public static String historyToString(HistoryManager manager) {
        String result = "";
        for (Task task : manager.getHistory()) { //Неправильно ставит запятые
            result = result + task.getId();
            result = result + ",";
        }
        return result;
    }

    private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        List<String> fileData = new ArrayList<>();
        if (file == null) {
            throw new IllegalArgumentException(NULL_VALUE_MESSAGE);
        } else {
            try {
                fileData = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                //Обработать исключения
            }


            if (fileData.size() > 1) {
                String data;
                int i;
                for (i = 1, data = fileData.get(i); !data.isEmpty(); i++, data = fileData.get(i)) {
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
                            manager.subtasks.put(subtask.getId(), subtask);
                            break;
                    }
                }
                i++;
                List<Integer> history = historyFromString(fileData.get(i));
                // Убрать повторяющийся код
                for (Integer key : history) {
                    Task task;
                    boolean taskFound = false;
                    while (!taskFound) {
                        task = manager.simpleTasks.get(key);
                        if (task != null) {
                            manager.historyManager.add(task);
                            taskFound = true;
                        }
                        task = manager.epicTasks.get(key);
                        if (task != null) {
                            manager.historyManager.add(task);
                            taskFound = true;
                        }
                        task = manager.subtasks.get(key);
                        if (task != null) {
                            manager.historyManager.add(task);
                            taskFound = true;
                        }
                    }
                }
            } else {
                // Что делать, в случае, если файл не пуст, но и не досточно большой для обработки?
            }

        }
        return manager;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_PATH))) {
            // Убрать повторяющийся код
            for (Integer key : simpleTasks.keySet()) {
                SimpleTask simpleTask = simpleTasks.get(key);
                writer.write(simpleTask.toString() + "\n");
            }
            for (Integer key : epicTasks.keySet()) {
                Epic epic = epicTasks.get(key);
                writer.write(epic.toString() + "\n");
            }
            for (Integer key : subtasks.keySet()) {
                Subtask subtask = subtasks.get(key);
                writer.write(subtask.toString() + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

    }


}
