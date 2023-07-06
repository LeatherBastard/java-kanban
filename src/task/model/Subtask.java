package task.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task implements StatusChangeable, TimeChangeable {
    private int epicOwnerId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getEpicOwnerId() {
        return epicOwnerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            Subtask other = (Subtask) obj;
            return Objects.equals(epicOwnerId, other.epicOwnerId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(epicOwnerId);
    }

    public void setEpicOwnerId(int epicOwnerId) {
        this.epicOwnerId = epicOwnerId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return id + ",SUBTASK," + name + "," + status + "," + description + "," + epicOwnerId;
    }


}
