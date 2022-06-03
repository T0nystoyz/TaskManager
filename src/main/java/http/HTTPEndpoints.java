package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPEndpoints {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final HTTPTaskManager manager;

    static {
        try {
            manager = new HTTPTaskManager(URI.create("http://localhost:8078"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Task task = gson.fromJson(body, Task.class);
            String response = "";
            switch (method) {
                case "POST":
                    if (query == null) {
                        manager.createNewTask(task);
                        response = gson.toJson(task.getId()) + "task has been created";
                    } else {
                        manager.updateTask(task);
                        response = gson.toJson(task.getId()) + "task has been updated";
                    }

                    break;
                case "GET":
                    response = gson.toJson(InMemoryTaskManager.getTasks());
                    break;
                case "DELETE":
                    if (query != null) {
                        String id = query.split("=")[1];
                        response = gson.toJson(manager.removeTaskById(Integer.parseInt(id)));
                    }
                    break;
                default:
                    response = "unknown method detected";
            }
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Epic epic = gson.fromJson(body, Epic.class);
            switch (method) {
                case "POST":
                    if (query == null) {
                        manager.createNewTask(epic);
                    } else {
                        manager.updateTask(epic);
                    }
                    response = gson.toJson(epic.getId());
                    break;
                case "GET":
                    if (query == null) {
                        response = gson.toJson(manager.showAllTasksByType("epic"));
                    } else {
                        String id = query.split("=")[1];
                        response = gson.toJson(manager.getAllSubtasksOfEpicByEpicId(Integer.parseInt(id)));
                    }
                    break;
                case "DELETE":
                    if (query != null) {
                        String id = query.split("=")[1];
                        response = gson.toJson(manager.removeTaskById(Integer.parseInt(id)));
                    }
                    break;
                default:
                    response = gson.toJson("unknown method detected");
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Subtask subtask = gson.fromJson(body, Subtask.class);
            switch (method) {
                case "POST":
                    if (query == null) {
                        manager.createNewTask(subtask);
                    } else {
                        manager.updateTask(subtask);
                    }
                    response = gson.toJson(subtask.getId());
                    break;
                case "GET":
                    response = gson.toJson(manager.showAllTasksByType("subtask"));
                    break;
                case "DELETE":
                    if (query != null) {
                        String id = query.split("=")[1];
                        response = gson.toJson(manager.removeTaskById(Integer.parseInt(id)));
                    }
                    break;
                default:
                    response = gson.toJson("unknown method detected");
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class GetAllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            String response;
            if ("GET".equals(method)) {
                response = gson.toJson(manager.getAllTasks());
            } else {
                response = gson.toJson("unknown method detected");
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class ClearAllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            String response;
            if (method.equals("DELETE")) {
                manager.clearAllTasks();
                response = gson.toJson("all tasks are cleared");
            } else {
                response = gson.toJson("unknown method detected");
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class GetPrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            String response;
            if (method.equals("GET")) {
                response = gson.toJson(manager.getPrioritizedTasks());
            } else response = gson.toJson("unknown method detected");
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            String response;
            if (method.equals("GET")) {
                response = gson.toJson(manager.history());
            } else {
                response = gson.toJson("unknown method detected");
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

}
