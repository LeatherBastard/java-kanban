package task.model;

import java.util.Objects;

public class Subtask extends Task {
    private String epicName;

    public Subtask(String name, String description) {
        super(name, description);
        id = hashCode();
        status = TASK_STATUS_NEW;
    }

    public String getEpicName() {
        return epicName;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            Subtask other = (Subtask) obj;
            return Objects.equals(epicName, other.epicName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(epicName);
    }

    void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + id + "', " +
                "name='" + name + "', " +
                "description='" + description + "', " +
                "status='" + status + "', " +
                "epicName='" + epicName + "'" +
                "}";
    }
}
