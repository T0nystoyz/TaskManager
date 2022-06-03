package taskManagers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.*;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tasks.Status.DONE;
import static tasks.Status.IN_PROGRESS;

class HTTPTaskServerTest {

    final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    HTTPTaskServer httpTaskServer;
    KVServer KVServer;
    HttpClient client = HttpClient.newHttpClient();
    private TaskManager taskManager;

    {
        try {
            httpTaskServer = new HTTPTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        KVServer = new KVServer();
        KVServer.start();
        taskManager = new HTTPTaskManager(URI.create("http://localhost:8078"));
        httpTaskServer.start();
        taskManager.clearAllTasks();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        KVServer.stop();
    }

    @Test
    public void test1_getTasksFromServerShouldSaveAndReturnMapOfTasks() throws IOException, InterruptedException {
        int task1Id = taskManager.createNewTask(new Task("Задача 1", "текст", IN_PROGRESS,
                LocalDateTime.parse("2022-04-27 10:15:30", formatter),
                Duration.of(10, ChronoUnit.MINUTES)));
        int task2Id = taskManager.createNewTask(new Task("Задача 2", "текст", DONE));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<Map<Integer, Task>>() {
        }.getType();
        Map<Integer, Task> tasksFromServer = gson.fromJson(response.body(), type);
        assertEquals(taskManager.getTaskById(task1Id), tasksFromServer.get(1));
        assertEquals(taskManager.getTaskById(task2Id), tasksFromServer.get(2));
        taskManager.clearAllTasks();
        tasksFromServer.get(1);
        System.out.println(response.body());
        assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(1));
        assertEquals(tasksFromServer.get(1), new Task(1, "Задача 1", "текст", IN_PROGRESS,
                LocalDateTime.parse("2022-04-27 10:15:30", formatter),
                Duration.of(10, ChronoUnit.MINUTES)));
    }
}
