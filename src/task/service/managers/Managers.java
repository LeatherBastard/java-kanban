package task.service.managers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import task.service.managers.adapter.LocalDateTimeAdapter;
import task.service.managers.history.HistoryManager;
import task.service.managers.history.InMemoryHistoryManager;
import task.service.managers.task.InMemoryTaskManager;
import task.service.managers.task.TaskManager;

import java.time.LocalDateTime;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return builder.create();
    }
}
