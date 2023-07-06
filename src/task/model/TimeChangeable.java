package task.model;

import java.time.Duration;
import java.time.LocalDateTime;

public interface TimeChangeable {
    void setDuration(Duration duration);

    void setStartTime(LocalDateTime startTime);
}
