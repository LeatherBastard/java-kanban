package task.service.managers.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.*;


import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.*;
import static task.service.managers.task.TaskType.*;

class TaskManagerTest {
    private TaskManager taskManager;

    private Task getTask(TaskType type) {
        switch (type) {
            case TASK:
                return new SimpleTask("task", "task");
            case SUBTASK:
                return new Subtask("subtask", "subtask");
            case EPIC:
                return new Epic("epic", "epic");
            default:
                return null;
        }
    }

    @BeforeEach
    void initialize() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddSimpleTaskWithOrdinaryTask() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(task);
        assertTrue(taskManager.getAllSimpleTasks().contains(task));
    }

    @Test
    void testAddSimpleTaskWithNullTask() {
        SimpleTask task = null;
        assertThrows(
                NullPointerException.class,
                () -> taskManager.addSimpleTask(task));

    }

    @Test
    void testAddSubtaskWithOrdinarySubtask() {
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
    }

    @Test
    void testShouldAddSubtaskChangeEpicStatus() {
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addEpicTask(epic);
        subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
        taskManager.addSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicTaskById(epic.getId()).getStatus());
    }

    @Test
    void testAddSubTaskWithNullSubtask() {
        Subtask subtask = null;
        assertThrows(
                NullPointerException.class,
                () -> taskManager.addSubtask(subtask));

    }

    @Test
    void testAddEpicTaskWithOrdinaryEpic() {
        Epic epic = (Epic) getTask(EPIC);
        taskManager.addEpicTask(epic);
        assertTrue(taskManager.getAllEpicTasks().contains(epic));
    }

    @Test
    void testAddEpicTaskWhenEpicContainsSubtasks() {
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Epic epic = (Epic) getTask(EPIC);
        epic.addSubtask(subtask);
        taskManager.addEpicTask(epic);
        for (Subtask task : epic.getSubtasks()) {
            assertTrue(taskManager.getAllSubtasks().contains(task));
        }
    }

    @Test
    void testAddEpicWithNullEpic() {
        Epic epic = null;
        assertThrows(
                NullPointerException.class,
                () -> taskManager.addEpicTask(epic));

    }

    @Test
    void testGetSimpleTaskByIdWithOrdinarySimpleTask() {
        SimpleTask expectedTask = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(expectedTask);
        assertTrue(taskManager.getSimpleTaskById(expectedTask.getId()).equals(expectedTask));
    }

    @Test
    void testGetSimpleTaskByIdWithWrongId() {
        assertNull(taskManager.getSimpleTaskById(0));
    }

    @Test
    void testGetSubtaskByIdWithOrdinarySubtask() {
        Subtask expectedTask = (Subtask) getTask(SUBTASK);
        taskManager.addSubtask(expectedTask);
        assertTrue(taskManager.getSubtaskById(expectedTask.getId()).equals(expectedTask));
    }

    @Test
    void testGetSubtaskTaskByIdWithWrongId() {
        assertNull(taskManager.getSubtaskById(0));
    }

    @Test
    void testGetEpicTaskByIdWithOrdinaryEpic() {
        Epic expectedTask = (Epic) getTask(EPIC);
        taskManager.addEpicTask(expectedTask);
        assertTrue(taskManager.getEpicTaskById(expectedTask.getId()).equals(expectedTask));
    }

    @Test
    void testGetEpicTaskByIdWithWrongId() {
        assertNull(taskManager.getEpicTaskById(0));
    }

    @Test
    void testGetAllSimpleTasksIfHaveTasks() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(task);
        assertTrue(taskManager.getAllSimpleTasks().contains(task));
    }

    @Test
    void testGetAllSimpleTasksIfHaveNoTasks() {
        assertTrue(taskManager.getAllSimpleTasks().isEmpty());
    }

    @Test
    void testGetAllSubtasksIfHaveTasks() {
        Subtask task = (Subtask) getTask(SUBTASK);
        taskManager.addSubtask(task);
        assertTrue(taskManager.getAllSubtasks().contains(task));
    }

    @Test
    void testGetAllSubtasksIfHaveNoTasks() {
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testGetAllEpicTasksIfHaveTasks() {
        Epic task = (Epic) getTask(EPIC);
        taskManager.addEpicTask(task);
        assertTrue(taskManager.getAllEpicTasks().contains(task));
    }

    @Test
    void testGetAllEpicTasksIfHaveNoTasks() {
        assertTrue(taskManager.getAllEpicTasks().isEmpty());
    }

    @Test
    void testGetHistoryIfIsNotEmpty() {
        SimpleTask simpleTask = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(simpleTask);
        taskManager.getSimpleTaskById(simpleTask.getId());
        assertTrue(taskManager.getHistory().contains(simpleTask));
    }

    @Test
    void testGetHistoryIfIsEmpty() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testRemoveSimpleTaskByIdIfProperId() {
        SimpleTask simpleTask = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(simpleTask);
        taskManager.removeSimpleTaskById(simpleTask.getId());
        assertFalse(taskManager.getAllSimpleTasks().contains(simpleTask));
    }

    @Test
    void testRemoveSimpleTaskByIdIfWrongId() {
        SimpleTask simpleTask = (SimpleTask) getTask(TASK);
        SimpleTask removedSimpleTask = new SimpleTask("simple", "task");
        taskManager.addSimpleTask(simpleTask);
        taskManager.addSimpleTask(removedSimpleTask);
        taskManager.removeSimpleTaskById(removedSimpleTask.getId());
        assertTrue(taskManager.getAllSimpleTasks().contains(simpleTask));
        assertFalse(taskManager.getAllSimpleTasks().contains(removedSimpleTask));
    }

    @Test
    void testRemoveSubtaskByIdIfProperId() {
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        subtask.setId(epic.getId());
        taskManager.addEpicTask(epic);
        taskManager.addSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        assertFalse(taskManager.getAllSubtasks().contains(subtask));
        assertFalse(taskManager.getAllEpicTasks().get(0).getSubtasks().contains(subtask));
    }

    @Test
    void testRemoveSubtaskByIdIfWrongId() {
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Subtask removedSubtask = new Subtask("sub", "task");
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(removedSubtask);
        taskManager.removeSubtaskById(removedSubtask.getId());
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
        assertFalse(taskManager.getAllSubtasks().contains(removedSubtask));
    }

    @Test
    void testRemoveEpicByIdIfProperId() {
        Subtask subtask = (Subtask) getTask(SUBTASK);
        Epic epic = (Epic) getTask(EPIC);
        taskManager.addEpicTask(epic);
        taskManager.addSubtask(subtask);
        taskManager.removeEpicTaskById(epic.getId());
        assertFalse(taskManager.getAllEpicTasks().contains(epic));
        assertFalse(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveEpicByIdIfWrongId() {
        Epic epic = (Epic) getTask(EPIC);
        Epic removedEpic = new Epic("epic", "task");
        taskManager.addEpicTask(epic);
        taskManager.addEpicTask(removedEpic);
        taskManager.removeEpicTaskById(removedEpic.getId());
        assertTrue(taskManager.getAllEpicTasks().contains(epic));
        assertFalse(taskManager.getAllEpicTasks().contains(removedEpic));
    }

    @Test
    void testRemoveAllSimpleTasksWithValues() {
        SimpleTask simpleTask = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(simpleTask);
        assertTrue(taskManager.getAllSimpleTasks().contains(simpleTask));
        taskManager.removeAllSimpleTasks();
        assertTrue(taskManager.getAllSimpleTasks().isEmpty());
    }

    @Test
    void testRemoveAllSimpleTasksWithoutValues() {
        taskManager.removeAllSimpleTasks();
        assertTrue(taskManager.getAllSimpleTasks().isEmpty());
    }


    @Test
    void testRemoveAllSubTasksWithValues() {
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addEpicTask(epic);
        subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
        taskManager.addSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllEpicTasks().get(0).getSubtasks().contains(subtask));
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveAllSubTasksWithoutValues() {
        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveAllEpicTasksWithValues() {
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addEpicTask(epic);
        subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
        taskManager.addSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
        assertTrue(taskManager.getAllEpicTasks().contains(epic));
        taskManager.removeAllEpicTasks();
        assertTrue(taskManager.getAllEpicTasks().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveAllEpicTasksWithoutValues() {
        taskManager.removeAllEpicTasks();
        assertTrue(taskManager.getAllEpicTasks().isEmpty());
    }

    @Test
    void testUpdateSimpleTaskWithOrdinaryTask() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(task);
        task.setName("epic");
        taskManager.updateSimpleTask(task);
        assertTrue(taskManager.getSimpleTaskById(task.getId()).getName().equals("epic"));
    }

    @Test
    void testUpdateSimpleTaskWithEmptyTask() {
        SimpleTask task = null;
        assertThrows(NullPointerException.class, () -> taskManager.updateSimpleTask(task));
    }

    @Test
    void testUpdateSubtaskWithOrdinaryTask() {
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addEpicTask(epic);
        subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
        taskManager.addSubtask(subtask);
        subtask.setName("changed subtask");
        taskManager.updateSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
        assertTrue(taskManager.getAllEpicTasks().get(0).getSubtasks().contains(subtask));
    }

    @Test
    void testUpdateSubtaskWithEmptyTask() {
        Subtask task = null;
        assertThrows(NullPointerException.class, () -> taskManager.updateSubtask(task));
    }

    @Test
    void testUpdateEpicTaskWithOrdinaryTask() {
        Epic epic = (Epic) getTask(EPIC);
        Epic anotherEpic = new Epic("another epic", "epic");
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addEpicTask(epic);
        subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
        taskManager.addSubtask(subtask);
        anotherEpic.setId(epic.getId());
        taskManager.updateEpicTask(anotherEpic);
        assertTrue(taskManager.getEpicTaskById(epic.getId()).getName().equals(anotherEpic.getName()));
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testUpdateEpicTaskWithEmptyTask() {
        Epic task = null;
        assertThrows(NullPointerException.class, () -> taskManager.updateEpicTask(task));
    }
}