package task.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicName(name);
        subtasks.add(subtask);
        checkStatus();
    }

    private Subtask getSubtask(String name) {
        for (Subtask subtask : subtasks) {
            if (subtask.name.equals(name)) {
                return subtask;
            }
        }
        return new Subtask("", "");
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public String getSubtaskStatus(String subtaskName) {
        return getSubtask(subtaskName).status;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            Epic other = (Epic) obj;
            return Objects.equals(subtasks, other.subtasks);
        }
        return false;
    }

    public void checkStatus() {
        if (subtasks.isEmpty() || isAllSubtasksNew()) status = TASK_STATUS_NEW;
        else if (isAllSubtasksDone()) status = TASK_STATUS_DONE;
        else status = TASK_STATUS_IN_PROGRESS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(subtasks);
    }

    private boolean isAllSubtasksNew() {
        return isAllSubtasksHaveStatus(TASK_STATUS_NEW);
    }

    private boolean isAllSubtasksDone() {
        return isAllSubtasksHaveStatus(TASK_STATUS_DONE);
    }

    private boolean isAllSubtasksHaveStatus(String status) {
        int count = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.status.equals(status))
                count++;
        }
        return count == subtasks.size();
    }

    public void removeSubtask(String name) {
        for (Subtask subtask : subtasks) {
            if (subtask.name.equals(name)) {
                subtasks.remove(subtask);
                break;
            }
        }
        checkStatus();
    }

    public void setSubtasks(List<Subtask> subtasks) {
        for (int i = 0; i < subtasks.size(); i++) {
            this.subtasks.add(subtasks.get(i));
            this.subtasks.get(i).setEpicName(this.name);
        }
        checkStatus();
    }

    public void setSubtaskStatus(String subtaskName, String status) {
        Subtask subtask = getSubtask(subtaskName);
        subtask.setStatus(status);
        checkStatus();
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id='" + id + "', " +
                "name='" + name + "', " +
                "description='" + description + "', " +
                "status='" + status + "', " +
                "subtasks=[";
        if (subtasks != null) {
            for (int i = 0; i < subtasks.size(); i++) {
                result = result + subtasks.get(i).toString();
                if (i != subtasks.size() - 1) {
                    result = result + ",";
                    result = result + "\n";
                }
            }
        } else {
            result = result + "null";
        }
        result = result + "]}";
        return result;
    }
}
