package http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static http.HTTPEndpoints.*;

public class HTTPTaskServer {
    private static final int PORT = 8008;
    private final HttpServer httpServer;


    public HTTPTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public void start() {
        httpServer.start();
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/", new GetAllTasksHandler());
        httpServer.createContext("/tasks/clear", new ClearAllTasksHandler());
        httpServer.createContext("/tasks/getPrioritized", new GetPrioritizedHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        System.out.println("http server running on " + PORT + " port");
    }
}