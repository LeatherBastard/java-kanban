package task.service.managers;


import task.service.managers.history.HistoryManager;
import task.service.managers.history.InMemoryHistoryManager;
import task.service.managers.task.InMemoryTaskManager;
import task.service.managers.task.TaskManager;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
