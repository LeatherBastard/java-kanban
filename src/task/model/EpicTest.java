package task.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static task.model.Task.formatter;
import static task.service.managers.task.TaskManagerTest.getTask;
import static task.service.managers.task.TaskType.*;

class EpicTest {
    private Epic epicExample;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void initializeEpic() {
        epicExample = new Epic("epicExample", "example");
        subtask1 = (Subtask) getTask(SUBTASK);
        subtask1.setId(3);
        subtask2 = (Subtask) getTask(SUBTASK);
    }

    @Test
    public void shouldStatusBeNewWhenNoSubtask() {
        assertEquals(TaskStatus.NEW, epicExample.getStatus());
    }

    @Test
    public void shouldStatusBeNewWhenSubtasksAreNew() {
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.NEW);
        epicExample.addSubtask(subtask1);
        epicExample.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epicExample.getStatus());
    }

    @Test
    public void shouldStatusBeDoneWhenSubtasksAreDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        epicExample.addSubtask(subtask1);
        epicExample.addSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epicExample.getStatus());
    }

    @Test
    public void shouldStatusBeInProgressWhenSubtasksAreNewAndDone() {
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);
        epicExample.addSubtask(subtask1);
        epicExample.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epicExample.getStatus());
    }

    @Test
    public void shouldStatusBeInProgressWhenSubtasksAreInProgress() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        epicExample.addSubtask(subtask1);
        epicExample.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epicExample.getStatus());
    }

    @Test
    public void testCalculateTimeIfNoSubtasks() {
        epicExample.calculateTime();
        assertEquals(epicExample.startTime, LocalDateTime.parse("01.01.1970 00:00", formatter));
        assertEquals(epicExample.getEndTime(), LocalDateTime.parse("01.01.1970 00:00", formatter));
        assertEquals(epicExample.duration, Duration.ofMinutes(0));
    }

    @Test
    public void testCalculateTimeWithSubtasks() {
        subtask2.setStartTime(subtask2.getStartTime().plusHours(2));
        subtask2.setDuration(subtask2.getDuration().plusMinutes(3));
        epicExample.addSubtask(subtask1);
        epicExample.addSubtask(subtask2);
        assertEquals(epicExample.startTime, subtask1.startTime);
        assertEquals(epicExample.getEndTime(), subtask2.getEndTime());
        assertEquals(epicExample.duration, Duration.ofMinutes(3));
    }
}