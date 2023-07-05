package task.service.managers.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.SimpleTask;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void initialize() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddSimpleTaskWithOrdinaryTask() {
        SimpleTask task = new SimpleTask("ordinaryTask", "justTask");
        taskManager.addSimpleTask(task);
        assertTrue(taskManager.getAllSimpleTasks().contains(task));
    }

    @Test
    void testAddSimpleTaskWithNullTask() {
        SimpleTask task = null;
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.addSimpleTask(task));

    }
}