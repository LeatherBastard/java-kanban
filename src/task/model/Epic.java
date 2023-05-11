package task.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static task.model.TaskStatus.*;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicOwnerId(id);
        subtasks.add(subtask);
        checkStatus();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
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
        if (subtasks.isEmpty() || isAllSubtasksNew()) status = NEW;
        else if (isAllSubtasksDone()) status = DONE;
        else status = IN_PROGRESS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(subtasks);
    }

    private boolean isAllSubtasksNew() {
        return isAllSubtasksHaveStatus(NEW);
    }

    private boolean isAllSubtasksDone() {
        return isAllSubtasksHaveStatus(DONE);
    }

    private boolean isAllSubtasksHaveStatus(TaskStatus status) {
        int count = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.status.equals(status))
                count++;
        }
        return count == subtasks.size();
    }

    public void removeSubtask(int id) {
        for (Subtask subtask : subtasks) {
            if (subtask.id == id) {
                subtasks.remove(subtask);
                break;
            }
        }
        checkStatus();
    }

    public void setSubtasks(List<Subtask> subtasks) {
        for (int i = 0; i < subtasks.size(); i++) {
            this.subtasks.add(subtasks.get(i));
            this.subtasks.get(i).setEpicOwnerId(id);
        }
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
