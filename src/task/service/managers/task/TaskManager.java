package task.service.managers.task;

import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.model.Task;

import java.util.List;

public interface TaskManager {
    void addSimpleTask(SimpleTask simpleTask);

    void addSubtask(Subtask subtask);

    void addEpicTask(Epic epic);

    SimpleTask getSimpleTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicTaskById(int id);

    List<SimpleTask> getAllSimpleTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpicTasks();

    List<Task> getHistory();

    void removeSimpleTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicTaskById(int id);

    void removeAllSimpleTasks();

    void removeAllSubtasks();

    void removeAllEpicTasks();

    void updateSimpleTask(SimpleTask simpleTask);

    void updateSubtask(Subtask subtask);

    void updateEpicTask(Epic epic);
}
