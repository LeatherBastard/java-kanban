package task.model;

public class SimpleTask extends Task implements StatusChangeable {

    public SimpleTask(String name, String description) {
        super(name, description);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                "id='" + id + "', " +
                "name='" + name + "', " +
                "description='" + description + "', " +
                "status='" + status + "', " +
                "}";
    }
}
