package task.service.managers.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import task.model.SimpleTask;
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
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String GET_TASKS_ENDPOINT = "/tasks/task";
    private static final String GET_TASK_ENDPOINT = "/tasks/task/?id=";
    private static final String ADD_UPDATE_TASK_ENDPOINT = "POST/tasks/task/BODY:{task..}";
    private static final String DELETE_TASK_ENDPOINT = "DELETE/tasks/task/?id=";
    private static final String DELETE_TASKS = "DELETE/tasks/task/";

    private final HttpServer server;
    private final Gson gson;
    TaskManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = Managers.getGson();
        manager = Managers.getDefault();
    }

    public void start() {
        System.out.println("Started TaskServer " + PORT);
        server.createContext(GET_TASK_ENDPOINT, this::handleTasks);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped Task server " + PORT);
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("^/task/tasks/+$", path)) {
                    writeResponse(httpExchange, gson.toJson(manager.getAllSimpleTasks()), 200);
                } else if (Pattern.matches("^/task/tasks/\\?id=\\d+$", path)) {
                    Optional<Integer> optTaskId = getPostId(path);
                    if (optTaskId.isPresent()) {
                        int taskID = optTaskId.get();
                        Optional<SimpleTask> simpleTaskById = Optional.ofNullable(manager.getSimpleTaskById(taskID));
                        if (simpleTaskById.isPresent()) writeResponse(httpExchange, gson.toJson(simpleTaskById), 200);
                        else writeResponse(httpExchange, "Задача с идентификатором " + taskID + " не найдена", 404);
                    } else {
                        writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
                    }
                } else writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
                break;
            case "POST":
                if (Pattern.matches("^/task/tasks/\\?id=\\d+$", path)) {
                    List<String> contentTypeValues = httpExchange.getRequestHeaders().get("Content-type");
                    if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                        InputStream inputStreamBody = httpExchange.getRequestBody();
                        String stringBody = new String(inputStreamBody.readAllBytes(), DEFAULT_CHARSET);
                        try {
                            SimpleTask simpleTask = gson.fromJson(stringBody, SimpleTask.class);
                            manager.addSimpleTask(simpleTask);
                            writeResponse(httpExchange, "Задача добавлена", 200);
                        } catch (JsonSyntaxException exception) {
                            writeResponse(httpExchange, "Incorrect JSON", 404);
                        }
                    } else {
                        writeResponse(httpExchange, "Incorrect content type", 400);
                    }
                } else writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
                break;
            case "DELETE":
                if (Pattern.matches("^/task/tasks/+$", path)) {
                    manager.removeAllSimpleTasks();
                    writeResponse(httpExchange, "Все задачи были удалены", 200);
                } else if (Pattern.matches("^/task/tasks/\\?id=\\d+$", path)) {
                    Optional<Integer> optTaskId = getPostId(path);
                    if (optTaskId.isPresent()) {
                        int taskID = optTaskId.get();
                        Optional<SimpleTask> removedTask = Optional.ofNullable(manager.removeSimpleTaskById(taskID));
                        if (removedTask.isPresent())
                            writeResponse(httpExchange, "Задача с идентификатором " + taskID + " была удалена", 200);
                        else writeResponse(httpExchange, "Задача с идентификатором " + taskID + " не найдена", 404);
                    } else {
                        writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
                    }
                } else {
                    writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
                }
                break;
            default:
                writeResponse(httpExchange, "Incorrect request method", 400);
        }
    }


    private void handleSubtasks() {

    }

    private void handleEpics() {

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
