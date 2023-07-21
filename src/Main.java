import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.client.KVTaskClient;
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
        KVTaskClient client = new KVTaskClient("http://localhost:8078");

        client.put("Taskskey", "abcd,dada,dasda");
        System.out.println(client.load("Taskskey"));
        client = new KVTaskClient("http://localhost:8078");
        System.out.println(client.load("Taskskey"));
    }
}
