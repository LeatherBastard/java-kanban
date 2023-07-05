package task.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epicExample;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void initializeEpic() {
        epicExample = new Epic("epicExample", "example");
        subtask1 = new Subtask("subtask1", "1");
        subtask2 = new Subtask("subtask2", "2");
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

}