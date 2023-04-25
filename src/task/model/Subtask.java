package task.model;

import java.util.Objects;

public class Subtask extends Task implements StatusChangeable{
    private String epicName;

    public Subtask(String name, String description) {
        super(name, description);
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
                "epicName='" + epicName + "'" +
                "}";
    }
}
