package task.service.managers.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import task.model.Epic;
import task.model.SimpleTask;
import task.model.Subtask;
import task.service.managers.Managers;
import task.service.managers.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public static final String SERVER_URL = "http://localhost:" + PORT;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String SERVER_START_MESSAGE = "Started TaskServer ";
    private static final String SERVER_STOP_MESSAGE = "Stopped TaskServer ";


    public static final String TASK_WAS_NOT_FOUND_MESSAGE = "id task was not found";
    public static final String TASK_INCORRECT_ID_MESSAGE = "Incorrect task id";
    public static final String TASK_ADD_MESSAGE = "Task was added";
    public static final String TASK_REMOVE_MESSAGE = "id task was removed";
    public static final String TASKS_REMOVE_MESSAGE = "All tasks were removed";

    public static final String SUBTASK_WAS_NOT_FOUND_MESSAGE = "id subtask was not found";
    public static final String SUBTASK_INCORRECT_ID_MESSAGE = "Incorrect subtask id";
    public static final String SUBTASK_ADD_MESSAGE = "Subtask was added";
    public static final String SUBTASK_REMOVE_MESSAGE = "id subtask was removed";
    public static final String SUBTASKS_REMOVE_MESSAGE = "All subtasks were removed";

    public static final String EPIC_WAS_NOT_FOUND_MESSAGE = "id epic was not found";
    public static final String EPIC_INCORRECT_ID_MESSAGE = "Incorrect epic id";
    public static final String EPIC_ADD_MESSAGE = "Epic was added";
    public static final String EPIC_REMOVE_MESSAGE = "id epic was removed";
    public static final String EPICS_REMOVE_MESSAGE = "All epics were removed";

    public static final String ENDPOINT_NOT_FOUND_MESSAGE = "There is no such endpoint";
    public static final String REQUEST_METHOD_INCORRECT_MESSAGE = "Incorrect request method";
    public static final String JSON_INCORRECT_MESSAGE = "Incorrect JSON";
    public static final String CONTENT_TYPE_INCORRECT_MESSAGE = "Incorrect content type";
    private static final String HEADER_CONTENT_TYPE = "Content-type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String GET_REQUEST_METHOD = "GET";
    private static final String POST_REQUEST_METHOD = "POST";
    private static final String DELETE_REQUEST_METHOD = "DELETE";
    public static final String GET_TASKS_ENDPOINT = "/tasks/task/";
    public static final String GET_SUBTASKS_ENDPOINT = "/tasks/subtask/";
    public static final String GET_EPICS_ENDPOINT = "/tasks/epic/";
    public static final String GET_EPIC_SUBTASKS_ENDPOINT = "/tasks/subtask/epic/";
    public static final String GET_TASKS_HISTORY_ENDPOINT = "/tasks/history";
    public static final String GET_TASKS_PRIORITIZED_ENDPOINT = "/tasks/";

    private final HttpServer server;
    private final Gson gson;
    TaskManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = Managers.getGson();
        manager = Managers.getDefault();
    }

    public void start() {
        System.out.println(SERVER_START_MESSAGE + PORT);
        server.createContext(GET_TASKS_ENDPOINT, this::handleTasks);
        server.createContext(GET_SUBTASKS_ENDPOINT, this::handleSubtasks);
        server.createContext(GET_EPICS_ENDPOINT, this::handleEpics);
        server.createContext(GET_EPIC_SUBTASKS_ENDPOINT, this::handleEpicSubtasks);
        server.createContext(GET_TASKS_HISTORY_ENDPOINT, this::handleTasksHistory);
        server.createContext(GET_TASKS_PRIORITIZED_ENDPOINT, this::handlePrioritizedTasks);

        server.start();
    }

    public void stop() {
        System.out.println(SERVER_STOP_MESSAGE + PORT);
        server.stop(0);
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_TASKS_ENDPOINT)) {
                        writeResponse(httpExchange, gson.toJson(manager.getAllSimpleTasks()), 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_TASKS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<SimpleTask> simpleTaskById = Optional.ofNullable(manager.getSimpleTaskById(taskID));
                            if (simpleTaskById.isPresent())
                                writeResponse(httpExchange, gson.toJson(simpleTaskById.get()), 200);
                            else writeResponse(httpExchange, taskID + " " + TASK_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, TASK_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case POST_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_TASKS_ENDPOINT)) {
                        List<String> contentTypeValues = httpExchange.getRequestHeaders().get(HEADER_CONTENT_TYPE);
                        if ((contentTypeValues != null) && (contentTypeValues.contains(HEADER_CONTENT_TYPE_VALUE))) {
                            InputStream inputStreamBody = httpExchange.getRequestBody();
                            String stringBody = new String(inputStreamBody.readAllBytes(), DEFAULT_CHARSET);
                            try {
                                SimpleTask simpleTask = gson.fromJson(stringBody, SimpleTask.class);
                                manager.addSimpleTask(simpleTask);
                                writeResponse(httpExchange, TASK_ADD_MESSAGE, 200);
                            } catch (JsonSyntaxException exception) {
                                writeResponse(httpExchange, JSON_INCORRECT_MESSAGE, 400);
                            }
                        } else {
                            writeResponse(httpExchange, CONTENT_TYPE_INCORRECT_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case DELETE_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_TASKS_ENDPOINT)) {
                        manager.removeAllSimpleTasks();
                        writeResponse(httpExchange, TASKS_REMOVE_MESSAGE, 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_TASKS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<SimpleTask> removedTask = Optional.ofNullable(manager.removeSimpleTaskById(taskID));
                            if (removedTask.isPresent())
                                writeResponse(httpExchange, +taskID + " " + TASK_REMOVE_MESSAGE, 200);
                            else writeResponse(httpExchange, taskID + " " + TASK_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, TASK_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else {
                        writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    }
                    break;
                default:
                    writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }
    }


    private void handleSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
                        writeResponse(httpExchange, gson.toJson(manager.getAllSubtasks()), 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<Subtask> subtaskById = Optional.ofNullable(manager.getSubtaskById(taskID));
                            if (subtaskById.isPresent())
                                writeResponse(httpExchange, gson.toJson(subtaskById.get()), 200);
                            else writeResponse(httpExchange, taskID + " " + SUBTASK_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, SUBTASK_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case POST_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
                        List<String> contentTypeValues = httpExchange.getRequestHeaders().get(HEADER_CONTENT_TYPE);
                        if ((contentTypeValues != null) && (contentTypeValues.contains(HEADER_CONTENT_TYPE_VALUE))) {
                            InputStream inputStreamBody = httpExchange.getRequestBody();
                            String stringBody = new String(inputStreamBody.readAllBytes(), DEFAULT_CHARSET);
                            try {
                                Subtask subtask = gson.fromJson(stringBody, Subtask.class);
                                manager.addSubtask(subtask);
                                writeResponse(httpExchange, SUBTASK_ADD_MESSAGE, 200);
                            } catch (JsonSyntaxException exception) {
                                writeResponse(httpExchange, JSON_INCORRECT_MESSAGE, 400);
                            }
                        } else {
                            writeResponse(httpExchange, CONTENT_TYPE_INCORRECT_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case DELETE_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
                        manager.removeAllSubtasks();
                        writeResponse(httpExchange, SUBTASKS_REMOVE_MESSAGE, 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<Subtask> removedSubtask = Optional.ofNullable(manager.removeSubtaskById(taskID));
                            if (removedSubtask.isPresent())
                                writeResponse(httpExchange, +taskID + " " + SUBTASK_REMOVE_MESSAGE, 200);
                            else writeResponse(httpExchange, taskID + " " + SUBTASK_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, SUBTASK_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else {
                        writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    }
                    break;
                default:
                    writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleEpics(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
                        writeResponse(httpExchange, gson.toJson(manager.getAllEpicTasks()), 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_EPICS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<Epic> epicById = Optional.ofNullable(manager.getEpicTaskById(taskID));
                            if (epicById.isPresent()) writeResponse(httpExchange, gson.toJson(epicById.get()), 200);
                            else writeResponse(httpExchange, taskID + " " + EPIC_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, EPIC_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case POST_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
                        List<String> contentTypeValues = httpExchange.getRequestHeaders().get(HEADER_CONTENT_TYPE);
                        if ((contentTypeValues != null) && (contentTypeValues.contains(HEADER_CONTENT_TYPE_VALUE))) {
                            InputStream inputStreamBody = httpExchange.getRequestBody();
                            String stringBody = new String(inputStreamBody.readAllBytes(), DEFAULT_CHARSET);
                            try {
                                Epic epic = gson.fromJson(stringBody, Epic.class);
                                manager.addEpicTask(epic);
                                writeResponse(httpExchange, EPIC_ADD_MESSAGE, 200);
                            } catch (JsonSyntaxException exception) {
                                writeResponse(httpExchange, JSON_INCORRECT_MESSAGE, 400);
                            }
                        } else {
                            writeResponse(httpExchange, CONTENT_TYPE_INCORRECT_MESSAGE, 400);
                        }
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                case DELETE_REQUEST_METHOD:
                    if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
                        manager.removeAllEpicTasks();
                        writeResponse(httpExchange, EPICS_REMOVE_MESSAGE, 200);
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_EPICS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<Epic> removedEpic = Optional.ofNullable(manager.removeEpicTaskById(taskID));
                            if (removedEpic.isPresent())
                                writeResponse(httpExchange, +taskID + " " + EPIC_REMOVE_MESSAGE, 200);
                            else writeResponse(httpExchange, taskID + " " + EPIC_WAS_NOT_FOUND_MESSAGE, 404);
                        } else {
                            writeResponse(httpExchange, EPIC_INCORRECT_ID_MESSAGE, 400);
                        }
                    } else {
                        writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    }
                    break;
                default:
                    writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleEpicSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals(GET_REQUEST_METHOD)) {
                if (pathMatchesByIdTaskEndpoint(path, GET_EPIC_SUBTASKS_ENDPOINT)) {
                    Optional<Integer> optTaskId = getPostId(path);
                    if (optTaskId.isPresent()) {
                        int taskID = optTaskId.get();
                        Optional<Epic> epicById = Optional.ofNullable(manager.getEpicTaskById(taskID));
                        if (epicById.isPresent())
                            writeResponse(httpExchange, gson.toJson(epicById.get().getSubtasks()), 200);
                        else writeResponse(httpExchange, taskID + " " + EPIC_WAS_NOT_FOUND_MESSAGE, 404);
                    }
                } else {
                    writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                }
            } else {
                writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleTasksHistory(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals(GET_REQUEST_METHOD)) {
                if (pathMatchesAllTasksEndpoint(path, GET_TASKS_HISTORY_ENDPOINT))
                    writeResponse(httpExchange, gson.toJson(manager.getHistory()), 200);
                else {
                    writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                }
            } else {
                writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }

    }

    private void handlePrioritizedTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals(GET_REQUEST_METHOD)) {
                if (pathMatchesAllTasksEndpoint(path, GET_TASKS_PRIORITIZED_ENDPOINT))
                    writeResponse(httpExchange, gson.toJson(manager.getPrioritizedTasks()), 200);
                else {
                    writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                }
            } else {
                writeResponse(httpExchange, REQUEST_METHOD_INCORRECT_MESSAGE, 400);
            }
        } finally {
            httpExchange.close();
        }
    }

    private boolean pathMatchesAllTasksEndpoint(String path, String endpoint) {

        return Pattern.matches("^" + endpoint + "$", path);
    }


    private boolean pathMatchesByIdTaskEndpoint(String path, String endpoint) {
        return Pattern.matches("^" + endpoint + "\\?id=\\d+$", path);
    }

    private Optional<Integer> getPostId(String path) {
        String postID = path.substring(path.indexOf("=") + 1);
        int id;
        try {
            id = Integer.parseInt(postID);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(id);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

}
