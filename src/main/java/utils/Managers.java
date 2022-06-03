package utils;

import http.HTTPTaskManager;
import managers.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Managers {

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HTTPTaskManager(URI.create("http://localhost:8078"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFromFile(File file) {
        return new FileBackedTasksManager();
    }
}