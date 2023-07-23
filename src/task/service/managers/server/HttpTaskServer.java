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

public class HttpTaskServer implements HttpTaskServerConstants {
    public static final int PORT = 8080;
    public static final String SERVER_URL = "http://localhost:" + PORT;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
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
        server.createContext(GET_TASK_ENDPOINT_HANDLER, this::handleTasks);
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
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
                    } else if (pathMatchesByIdTaskEndpoint(path, GET_EPIC_SUBTASKS_ENDPOINT)) {
                        Optional<Integer> optTaskId = getPostId(path);
                        if (optTaskId.isPresent()) {
                            int taskID = optTaskId.get();
                            Optional<Epic> epicById = Optional.ofNullable(manager.getEpicTaskById(taskID));
                            if (epicById.isPresent())
                                writeResponse(httpExchange, gson.toJson(epicById.get().getSubtasks()), 200);
                            else writeResponse(httpExchange, taskID + " " + EPIC_WAS_NOT_FOUND_MESSAGE, 404);
                        }
                    } else if (pathMatchesAllTasksEndpoint(path, GET_TASKS_PRIORITIZED_ENDPOINT)) {
                        writeResponse(httpExchange, gson.toJson(manager.getPrioritizedTasks()), 200);
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_SUBTASKS_ENDPOINT)) {
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
                    } else if (pathMatchesAllTasksEndpoint(path, GET_EPICS_ENDPOINT)) {
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
                    } else writeResponse(httpExchange, ENDPOINT_NOT_FOUND_MESSAGE, 404);
                    break;
                default:
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
