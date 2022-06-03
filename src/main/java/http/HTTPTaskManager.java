package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.FileBackedTasksManager;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVClient client;

    public HTTPTaskManager(URI path) throws IOException, InterruptedException {

        this.client = new KVClient(path);

    }

    @Override
    public void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String jsonTasks = gson.toJson(tasks);
        String jsonHistory = gson.toJson(super.history());
        try {
            client.put("tasks", jsonTasks);
            client.put("history", jsonHistory);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
