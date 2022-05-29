package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import managers.FileBackedTasksManager;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVClient client;

    public HTTPTaskManager(String path) {
        this.client = new KVClient(path);
    }

    @Override
    public void save() {
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(tasks).getAsJsonObject();
        client.put("tasks", json.getAsString());
        JsonObject historyJson = gson.toJsonTree(history()).getAsJsonObject();
        client.put("history", historyJson.getAsString());
    }
}
