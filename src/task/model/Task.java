package task.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static task.model.TaskStatus.*;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    protected Duration duration;

    protected LocalDateTime startTime;


    protected Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = NEW;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task other = (Task) obj;
        return id == other.id && Objects.equals(name, other.name) && Objects.equals(description, other.description) &&
                Objects.equals(status, other.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }
}
