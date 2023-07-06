package task.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimpleTask extends Task implements StatusChangeable, TimeChangeable {

    public SimpleTask(String name, String description) {
        super(name, description);
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + ",TASK," + name + "," + status + "," + description + ",";
    }
}
