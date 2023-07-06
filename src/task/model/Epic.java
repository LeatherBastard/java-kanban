package task.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static task.model.TaskStatus.*;

public class Epic extends Task {
    private LocalDateTime endTime;

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicOwnerId(id);
        subtasks.add(subtask);
        checkStatus();
        calculateTime();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
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

    public void calculateTime() {
        if (startTime != null && duration != null) {
            startTime = subtasks.stream().min(Comparator.comparing(subtask -> subtask.startTime)).get().startTime;
            endTime = subtasks.stream().max(Comparator.comparing(subtask -> subtask.startTime)).get().getEndTime();
            duration = subtasks.stream().map(subtask -> subtask.duration).reduce(Duration.ZERO, Duration::plus);
        } else {
            startTime = LocalDateTime.now();
            endTime = LocalDateTime.now();
            duration = Duration.ofMinutes(2);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
        calculateTime();
    }

    public void setSubtasks(List<Subtask> subtasks) {
        for (int i = 0; i < subtasks.size(); i++) {
            this.subtasks.add(subtasks.get(i));
            this.subtasks.get(i).setEpicOwnerId(id);
        }
        checkStatus();
        calculateTime();
    }

    @Override
    public String toString() {
        return id + ",EPIC," + name + "," + status + "," + description + "," + duration.toMinutes() + "," + startTime.format(formatter) + ",";
    }


}
