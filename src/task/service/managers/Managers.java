package task.service.managers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import task.service.managers.adapter.DurationAdapter;
import task.service.managers.adapter.LocalDateTimeAdapter;
import task.service.managers.history.HistoryManager;
import task.service.managers.history.InMemoryHistoryManager;
import task.service.managers.task.HttpTaskManager;
import task.service.managers.task.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {
    private static final String SERVER_URL = "http://localhost:8078";

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(SERVER_URL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls().serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return builder.create();
    }
}
