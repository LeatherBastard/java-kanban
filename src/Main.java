import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.server.HttpTaskServer;
import task.service.managers.server.KVServer;
import task.service.managers.task.HttpTaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static task.model.Task.formatter;
import static task.service.managers.task.TaskType.*;


public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new
                KVServer();
        server.start();
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        SimpleTask task = new SimpleTask("task", "task");
        SimpleTask simpleTask = new SimpleTask("task", "task");
        simpleTask.setId(0);
        simpleTask.setDuration(Duration.ZERO);
        simpleTask.setStartTime(LocalDateTime.parse("07.07.2023 08:53", formatter));


        Subtask subtask = new Subtask("subtask", "subtask");
        subtask.setId(1);
        subtask.setDuration(Duration.ZERO);
        subtask.setStartTime(LocalDateTime.parse("07.07.2023 18:40", formatter));
        Epic epic = new Epic("epic", "epic");
        epic.setId(2);
        epic.calculateTime();
        manager.addSimpleTask(task);
        manager.addSubtask(subtask);
        manager.addEpicTask(epic);
        manager.getSimpleTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicTaskById(epic.getId());
        List<Task> tasks = manager.getHistory();
        manager = new HttpTaskManager("http://localhost:8078");
    }
}
