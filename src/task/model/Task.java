package task.model;

import java.util.Objects;

public abstract class Task {
    public static final String TASK_STATUS_NEW = "NEW";
    public static final String TASK_STATUS_IN_PROGRESS = "IN PROGRESS";
    public static final String TASK_STATUS_DONE = "DONE";
    protected int id;
    protected String name;
    protected String description;
    protected String status;

    protected Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TASK_STATUS_NEW;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
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
