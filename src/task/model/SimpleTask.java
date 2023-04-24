package task.model;

public class SimpleTask extends Task {

    public SimpleTask(String name, String description) {
        super(name, description);
        id = hashCode();
        status = TASK_STATUS_NEW;
    }

    public void setStatus(String status) {
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
