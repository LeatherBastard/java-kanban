package task.service.managers.task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;
import task.service.managers.server.KVServer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.service.managers.task.TaskManagerTest.getTask;
import static task.service.managers.task.TaskType.*;

class HttpTaskManagerTest {

    private HttpTaskManager manager;
    private KVServer server;

    @BeforeEach
    public void initialize() {
        try {
            server = new KVServer();
            server.start();
            manager = new HttpTaskManager("http://localhost:8078");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @AfterEach
    public void serverStop() {
        try {
            server.stop();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    void testLoadFromServerIfTaskListEmpty() {
        assertTrue(manager.getHistory().isEmpty());
        manager = new HttpTaskManager("http://localhost:8078");
        assertTrue(manager.getHistory().isEmpty());
    }


    @Test
        // @Disabled
    void testLoadFromServerIfTaskListNotEmpty() {
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
        List<Task> tasksAfterReload = manager.getHistory();
        assertEquals(tasks, tasksAfterReload);
    }


    @Test
    void testLoadFromServerIfEpicHasSubtasks() {
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
    void testLoadFromServerIfHistoryEmpty() {
        manager.addSimpleTask((SimpleTask) getTask(TASK));
        assertFalse(manager.simpleTasks.isEmpty());
        manager = new HttpTaskManager("http://localhost:8078");
        assertTrue(manager.getHistory().isEmpty());
    }
}