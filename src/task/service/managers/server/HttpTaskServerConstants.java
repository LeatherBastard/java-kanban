package task.service.managers.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface HttpTaskServerConstants {
    String SERVER_START_MESSAGE = "Started TaskServer ";
    String SERVER_STOP_MESSAGE = "Stopped TaskServer ";
    String TASK_WAS_NOT_FOUND_MESSAGE = "id task was not found";
    String TASK_INCORRECT_ID_MESSAGE = "Incorrect task id";
    String TASK_ADD_MESSAGE = "Task was added";
    String TASK_REMOVE_MESSAGE = "id task was removed";
    String TASKS_REMOVE_MESSAGE = "All tasks were removed";
    String SUBTASK_WAS_NOT_FOUND_MESSAGE = "id subtask was not found";
    String SUBTASK_INCORRECT_ID_MESSAGE = "Incorrect subtask id";
    String SUBTASK_ADD_MESSAGE = "Subtask was added";
    String SUBTASK_REMOVE_MESSAGE = "id subtask was removed";
    String SUBTASKS_REMOVE_MESSAGE = "All subtasks were removed";
    String EPIC_WAS_NOT_FOUND_MESSAGE = "id epic was not found";
    String EPIC_INCORRECT_ID_MESSAGE = "Incorrect epic id";
    String EPIC_ADD_MESSAGE = "Epic was added";
    String EPIC_REMOVE_MESSAGE = "id epic was removed";
    String EPICS_REMOVE_MESSAGE = "All epics were removed";
    String ENDPOINT_NOT_FOUND_MESSAGE = "There is no such endpoint";
    String REQUEST_METHOD_INCORRECT_MESSAGE = "Incorrect request method";
    String JSON_INCORRECT_MESSAGE = "Incorrect JSON";
    String CONTENT_TYPE_INCORRECT_MESSAGE = "Incorrect content type";
    String HEADER_CONTENT_TYPE = "Content-type";
    String HEADER_CONTENT_TYPE_VALUE = "application/json";
    String GET_REQUEST_METHOD = "GET";
    String POST_REQUEST_METHOD = "POST";
    String DELETE_REQUEST_METHOD = "DELETE";
    String GET_TASK_ENDPOINT_HANDLER = "/tasks";
    String GET_TASKS_ENDPOINT = "/tasks/task/";
    String GET_SUBTASKS_ENDPOINT = "/tasks/subtask/";
    String GET_EPICS_ENDPOINT = "/tasks/epic/";
    String GET_EPIC_SUBTASKS_ENDPOINT = "/tasks/subtask/epic/";
    String GET_TASKS_HISTORY_ENDPOINT = "/tasks/history";
    String GET_TASKS_PRIORITIZED_ENDPOINT = "/tasks/";
}
