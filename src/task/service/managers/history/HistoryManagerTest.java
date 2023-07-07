package task.service.managers.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;

import static org.junit.jupiter.api.Assertions.*;
import static task.service.managers.task.TaskManagerTest.getTask;
import static task.service.managers.task.TaskType.*;

class HistoryManagerTest {
    private HistoryManager manager;

    @BeforeEach
    void initialize() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTaskWithEmptyHistory() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        manager.add(task);
        assertTrue(manager.getHistory().contains(task));
    }

    @Test
    void testAddTaskWIthNotEmptyHistory() {
        SimpleTask firstTask = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        secondTask.setId(2);
        manager.add(firstTask);
        manager.add(secondTask);
        assertTrue(manager.getHistory().contains(secondTask));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void testAddTaskWithDuplicateTask() {
        SimpleTask firstTask = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        manager.add(firstTask);
        manager.add(secondTask);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void testGetHistoryWithEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }


    @Test
    void testClearHistoryWithValues() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Epic epic = (Epic) getTask(EPIC);
        manager.add(task);
        manager.add(subtask);
        manager.add(epic);
        assertEquals(3, manager.getHistory().size());
        manager.clearHistory();
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void testGetHistoryWithValues() {
        SimpleTask firstTask = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        secondTask.setId(2);
        manager.add(firstTask);
        manager.add(secondTask);
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void testRemoveWithWrongIdOrEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
        manager.remove(2);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void testRemoveWitOneElement() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        manager.add(task);
        manager.remove(task.getId());
        assertFalse(manager.getHistory().contains(task));
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void testRemoveFirstElement() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        SimpleTask thirdTask = (SimpleTask) getTask(TASK);
        secondTask.setId(2);
        thirdTask.setId(3);
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);
        manager.remove(task.getId());
        assertFalse(manager.getHistory().contains(task));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void testRemoveMiddleElement() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        SimpleTask thirdTask = (SimpleTask) getTask(TASK);
        secondTask.setId(2);
        thirdTask.setId(3);
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);
        manager.remove(secondTask.getId());
        assertFalse(manager.getHistory().contains(secondTask));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void testRemoveLastElement() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        SimpleTask secondTask = (SimpleTask) getTask(TASK);
        SimpleTask thirdTask = (SimpleTask) getTask(TASK);
        secondTask.setId(2);
        thirdTask.setId(3);
        manager.add(task);
        manager.add(secondTask);
        manager.add(thirdTask);
        manager.remove(thirdTask.getId());
        assertFalse(manager.getHistory().contains(thirdTask));
        assertEquals(2, manager.getHistory().size());
    }

}