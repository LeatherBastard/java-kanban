package task.model;

import java.util.Objects;

public class Subtask extends Task implements StatusChangeable {
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

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + id + "', " +
                "name='" + name + "', " +
                "description='" + description + "', " +
                "status='" + status + "', " +
                "epicOwnerId='" + epicOwnerId + "'" +
                "}";
    }
}
