package task.service.managers.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.service.managers.task.FileBackedTaskManager.TASKS_PATH;
import static task.service.managers.task.FileBackedTaskManager.loadFromFile;
import static task.service.managers.task.TaskManagerTest.getTask;
import static task.service.managers.task.TaskType.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;

    @BeforeEach
    void initialize() {
        try {
            manager = FileBackedTaskManager.loadFromFile(new File(TASKS_PATH));
            manager.removeAllSimpleTasks();
            manager.removeAllEpicTasks();
            manager.historyManager.clearHistory();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        manager = loadFromFile(new File(TASKS_PATH));
        assertTrue(tasks.equals(manager.getHistory()));
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
        manager = loadFromFile(new File(TASKS_PATH));
        assertTrue(manager.getEpicTaskById(epic.getId()).getSubtasks().contains(subtask));
    }


    @Test
    void testLoadFromFileIfHistoryEmpty() {
        manager.addSimpleTask((SimpleTask) getTask(TASK));
        assertFalse(manager.simpleTasks.isEmpty());
        initialize();
        assertTrue(manager.getHistory().isEmpty());
    }


}