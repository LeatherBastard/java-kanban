package task.service.managers.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.model.Task.formatter;
import static task.service.managers.task.TaskType.*;

public class TaskManagerTest {
    private TaskManager taskManager;

    public static Task getTask(TaskType type) {
        switch (type) {
            case TASK:
                SimpleTask simpleTask = new SimpleTask("task", "task");
                simpleTask.setId(0);
                simpleTask.setDuration(Duration.ZERO);
                simpleTask.setStartTime(LocalDateTime.parse("07.07.2023 08:53", formatter));
                return simpleTask;
            case SUBTASK:
                Subtask subtask = new Subtask("subtask", "subtask");
                subtask.setId(1);
                subtask.setDuration(Duration.ZERO);
                subtask.setStartTime(LocalDateTime.parse("07.07.2023 18:40", formatter));
                return subtask;

            case EPIC:
                Epic epic = new Epic("epic", "epic");
                epic.setId(2);
                epic.calculateTime();
                return epic;
            default:
                throw new IllegalArgumentException();
        }
    }

    @BeforeEach
    void initialize() {
        taskManager = new InMemoryTaskManager();
    }

    @Nested
    class SimpleTaskTestMethods {
        @Test
        void testAddSimpleTaskWithOrdinaryTask() {
            SimpleTask task = (SimpleTask) getTask(TASK);
            taskManager.addSimpleTask(task);
            assertEquals(taskManager.getSimpleTaskById(task.getId()), task);
        }

        @Test
        void testAddSimpleTaskWithIntersectedDate() {
            SimpleTask task = (SimpleTask) getTask(TASK);
            SimpleTask anotherTask = (SimpleTask) getTask(TASK);
            anotherTask.setId(2);
            taskManager.addSimpleTask(task);
            assertTrue(taskManager.getAllSimpleTasks().contains(task));
            taskManager.addSimpleTask(anotherTask);
            assertFalse(taskManager.getAllSimpleTasks().contains(anotherTask));
        }

        @Test
        void testAddSimpleTaskWithEmptyTask() {
            SimpleTask task = null;
            assertThrows(
                    NullPointerException.class,
                    () -> taskManager.addSimpleTask(task));

        }

        @Test
        void testGetSimpleTaskByIdWithOrdinaryTask() {
            SimpleTask expectedTask = (SimpleTask) getTask(TASK);
            taskManager.addSimpleTask(expectedTask);
            assertEquals(taskManager.getSimpleTaskById(expectedTask.getId()), expectedTask);
        }

        @Test
        void testGetSimpleTaskByIdWithWrongId() {
            assertNull(taskManager.getSimpleTaskById(0));
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
            removedSimpleTask.setStartTime(simpleTask.getStartTime().plusDays(2));
            removedSimpleTask.setDuration(Duration.ofMinutes(2));
            taskManager.addSimpleTask(simpleTask);
            taskManager.addSimpleTask(removedSimpleTask);
            taskManager.removeSimpleTaskById(removedSimpleTask.getId());
            assertTrue(taskManager.getAllSimpleTasks().contains(simpleTask));
            assertFalse(taskManager.getAllSimpleTasks().contains(removedSimpleTask));
        }

        @Test
        void testRemoveAllSimpleTasksWithValues() {
            SimpleTask simpleTask = (SimpleTask) getTask(TASK);
            taskManager.addSimpleTask(simpleTask);
            taskManager.removeAllSimpleTasks();
            assertTrue(taskManager.getAllSimpleTasks().isEmpty());
        }

        @Test
        void testRemoveAllSimpleTasksWithoutValues() {
            assertTrue(taskManager.getAllSubtasks().isEmpty());
            taskManager.removeAllSimpleTasks();
            assertTrue(taskManager.getAllSimpleTasks().isEmpty());
        }

        @Test
        void testUpdateSimpleTaskWithOrdinaryTask() {
            SimpleTask task = (SimpleTask) getTask(TASK);
            taskManager.addSimpleTask(task);
            task.setName("changed task");
            taskManager.updateSimpleTask(task);
            assertEquals("changed task", taskManager.getSimpleTaskById(task.getId()).getName());
        }

        @Test
        void testUpdateSimpleTaskWithEmptyTask() {
            SimpleTask task = null;
            assertThrows(NullPointerException.class, () -> taskManager.updateSimpleTask(task));
        }

    }


    @Nested
    class SubtaskTestMethods {
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
        void testAddSubTaskWithEmptySubtask() {
            Subtask subtask = null;
            assertThrows(
                    NullPointerException.class,
                    () -> taskManager.addSubtask(subtask));
        }

        @Test
        void testGetSubtaskByIdWithOrdinaryTask() {
            Subtask expectedTask = (Subtask) getTask(SUBTASK);
            taskManager.addSubtask(expectedTask);
            assertEquals(taskManager.getSubtaskById(expectedTask.getId()), expectedTask);
        }

        @Test
        void testGetSubtaskTaskByIdWithWrongId() {
            assertNull(taskManager.getSubtaskById(0));
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
            removedSubtask.setStartTime(subtask.getStartTime().plusDays(2));
            removedSubtask.setDuration(Duration.ofDays(2));
            taskManager.addSubtask(subtask);
            taskManager.addSubtask(removedSubtask);
            taskManager.removeSubtaskById(removedSubtask.getId());
            assertTrue(taskManager.getAllSubtasks().contains(subtask));
            assertFalse(taskManager.getAllSubtasks().contains(removedSubtask));
        }

        @Test
        void testRemoveAllSubTasksWithValues() {
            Epic epic = (Epic) getTask(EPIC);
            Subtask subtask = (Subtask) getTask(SUBTASK);
            taskManager.addEpicTask(epic);
            subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
            taskManager.addSubtask(subtask);
            taskManager.removeAllSubtasks();
            assertFalse(taskManager.getAllEpicTasks().get(0).getSubtasks().contains(subtask));
            assertTrue(taskManager.getAllSubtasks().isEmpty());
        }

        @Test
        void testRemoveAllSubTasksWithoutValues() {
            assertTrue(taskManager.getAllSubtasks().isEmpty());
            taskManager.removeAllSubtasks();
            assertTrue(taskManager.getAllSubtasks().isEmpty());
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
    }

    @Nested
    class EpicTaskTestMethods {
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
        void testAddEpicWithEmptyEpic() {
            Epic epic = null;
            assertThrows(
                    NullPointerException.class,
                    () -> taskManager.addEpicTask(epic));

        }


        @Test
        void testGetEpicTaskByIdWithOrdinaryTask() {
            Epic expectedTask = (Epic) getTask(EPIC);
            taskManager.addEpicTask(expectedTask);
            assertEquals(taskManager.getEpicTaskById(expectedTask.getId()), expectedTask);
        }

        @Test
        void testGetEpicTaskByIdWithWrongId() {
            assertNull(taskManager.getEpicTaskById(0));
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
            Subtask subtask = (Subtask) getTask(SUBTASK);
            taskManager.addEpicTask(removedEpic);
            subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
            taskManager.addSubtask(subtask);
            taskManager.addEpicTask(epic);
            taskManager.removeEpicTaskById(removedEpic.getId());
            assertTrue(taskManager.getAllEpicTasks().contains(epic));
            assertFalse(taskManager.getAllEpicTasks().contains(removedEpic));
        }


        @Test
        void testRemoveAllEpicTasksWithValues() {
            Epic epic = (Epic) getTask(EPIC);
            Subtask subtask = (Subtask) getTask(SUBTASK);
            taskManager.addEpicTask(epic);
            subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
            taskManager.addSubtask(subtask);
            taskManager.removeAllEpicTasks();
            assertTrue(taskManager.getAllEpicTasks().isEmpty());
            assertTrue(taskManager.getAllSubtasks().isEmpty());
        }

        @Test
        void testRemoveAllEpicTasksWithoutValues() {
            assertTrue(taskManager.getAllSubtasks().isEmpty());
            taskManager.removeAllEpicTasks();
            assertTrue(taskManager.getAllEpicTasks().isEmpty());
        }


        @Test
        void testUpdateEpicTaskWithOrdinaryTask() {
            Epic epic = (Epic) getTask(EPIC);
            Epic anotherEpic = new Epic("another epic", "epic");
            anotherEpic.calculateTime();
            Subtask subtask = (Subtask) getTask(SUBTASK);
            taskManager.addEpicTask(epic);
            subtask.setEpicOwnerId(taskManager.getAllEpicTasks().get(0).getId());
            taskManager.addSubtask(subtask);
            anotherEpic.setId(epic.getId());
            taskManager.updateEpicTask(anotherEpic);
            String updatedEpicName = anotherEpic.getName();
            assertEquals(taskManager.getEpicTaskById(epic.getId()).getName(), updatedEpicName);
            assertTrue(taskManager.getAllSubtasks().isEmpty());
        }

        @Test
        void testUpdateEpicTaskWithEmptyTask() {
            Epic task = null;
            assertThrows(NullPointerException.class, () -> taskManager.updateEpicTask(task));
        }
    }

    @Test
    void testGetHistoryIfNotEmpty() {
        SimpleTask simpleTask = (SimpleTask) getTask(TASK);
        taskManager.addSimpleTask(simpleTask);
        taskManager.getSimpleTaskById(simpleTask.getId());
        assertTrue(taskManager.getHistory().contains(simpleTask));
    }

    @Test
    void testGetHistoryIfEmpty() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testGetPrioritizedTasksIfEmpty() {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void testGetPrioritizedTasksIfNotEmpty() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        Epic epic = (Epic) getTask(EPIC);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addSimpleTask(task);
        taskManager.addEpicTask(epic);
        taskManager.addSubtask(subtask);
        List<Task> expected = List.of(epic, task, subtask);
        assertEquals(expected, taskManager.getPrioritizedTasks());
    }

    @Test
    void testGetPrioritizedTasksIfTaskHaveNoDate() {
        SimpleTask task = (SimpleTask) getTask(TASK);
        Epic epic = (Epic) getTask(EPIC);
        task.setStartTime(null);
        Subtask subtask = (Subtask) getTask(SUBTASK);
        taskManager.addSimpleTask(task);
        taskManager.addEpicTask(epic);
        taskManager.addSubtask(subtask);
        int expectedPosition = taskManager.getPrioritizedTasks().size() - 1;
        assertEquals(task, taskManager.getPrioritizedTasks().get(expectedPosition));
    }
}