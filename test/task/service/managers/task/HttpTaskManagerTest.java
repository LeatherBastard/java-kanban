package task.service.managers.task;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.Managers;
import task.service.managers.server.KVServer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.service.managers.task.FileBackedTaskManager.TASKS_PATH;
import static task.service.managers.task.FileBackedTaskManager.loadFromFile;
import static task.service.managers.task.TaskManagerTest.getTask;
import static task.service.managers.task.TaskType.*;
import static task.service.managers.task.TaskType.TASK;

class HttpTaskManagerTest {

    private HttpTaskManager manager;
    private KVServer server;

    @BeforeEach
    public void initialize() {

        try {
            KVServer server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        manager = new HttpTaskManager("http://localhost:8078");
    }

    @AfterEach
    public void serverStop() {
        server.stop();
    }


    @Test
    void testLoadFromFileIfTaskListEmpty() {
        assertTrue(manager.getHistory().isEmpty());
        initialize();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void testLoadFromFileIfTaskListNotEmpty() throws IOException {
        SimpleTask task = (SimpleTask) getTask(TASK);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Epic epic = (Epic) getTask(EPIC);
        manager.addSimpleTask(task);
        manager.addSubtask(subtask);
        manager.addEpicTask(epic);
        manager.getSimpleTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicTaskById(epic.getId());
        List<Task> tasks = manager.getHistory();
        manager = new HttpTaskManager("http://localhost:8078");
        assertEquals(tasks, manager.getHistory());
    }


    @Test
    void testLoadFromFileIfEpicHasSubtasks() throws IOException {
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Epic epic = (Epic) getTask(EPIC);
        manager.addEpicTask(epic);
        subtask.setEpicOwnerId(manager.getAllEpicTasks().get(0).getId());
        manager.addSubtask(subtask);
        manager.getSubtaskById(subtask.getId());
        manager.getEpicTaskById(epic.getId());
        assertTrue(manager.getEpicTaskById(epic.getId()).getSubtasks().contains(subtask));
        manager = new HttpTaskManager("http://localhost:8078");
        assertTrue(manager.getEpicTaskById(epic.getId()).getSubtasks().contains(subtask));
    }


    @Test
    void testLoadFromFileIfHistoryEmpty() {
        manager.addSimpleTask((SimpleTask) getTask(TASK));
        assertFalse(manager.simpleTasks.isEmpty());
        manager = new HttpTaskManager("http://localhost:8078");
        assertTrue(manager.getHistory().isEmpty());
    }


}