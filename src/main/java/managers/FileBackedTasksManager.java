package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import utils.ManagerSaveException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file = new File("./src/file.csv");

    public FileBackedTasksManager() {
    }

    private static void extractTask(String line) {
        Task task;
        String[] lin = line.split(",");

        if (lin.length != 5 && lin.length != 6) {
            return;
        }

        switch (lin[1]) {
            case ("task"):
                task = new Task(Integer.parseInt(lin[0]),
                        lin[2],
                        lin[4],
                        Status.valueOf(lin[3]));
                tasks.put(task.getId(), task);
                break;

            case ("subtask"):
                task = new Subtask(Integer.parseInt(lin[0]),
                        lin[2],
                        lin[3],
                        Status.valueOf(lin[4]),
                        Integer.parseInt(lin[5]));
                tasks.put(task.getId(), task);
                break;

            case ("epic"):
                task = new Epic(Integer.parseInt(lin[0]),
                        lin[2],
                        lin[4]);
                tasks.put(task.getId(), task);
                break;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(String.valueOf(file)));
            for (String line : lines) {
                extractTask(line);
            }
        } catch (IOException e) {
            System.out.println("Неудачная попытка загрузки данных, загружен пустой менеджер");
            return taskManager;
        }
        return taskManager;
    }

    @Override
    public int createNewTask(Task task) {
        saveTask();
        return super.createNewTask(task);
    }

    public void saveTask() {
        try (Writer writer = new FileWriter(file)) {

            for (Task task : showAllTasksByType("task")) {
                writer.write(task.toString());
                writer.write("\n");
            }

            for (Task task : showAllTasksByType("epic")) {
                writer.write(task.toString());
                writer.write("\n");
            }

            for (Task task : showAllTasksByType("subtask")) {
                writer.write(task.toString());
                writer.write("\n");
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Неудачная попытка сохранения");

        }
    }

    public void saveHistory(InMemoryHistoryManager hm) {
        String history = hm.toString();
        try (Writer writer = new FileWriter(file, true)) {
            writer.write("\n");
            writer.write(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Неудачная попытка сохранения");
        }
    }

    @Override
    public void clearHistory() {
        super.clearHistory();
        saveTask();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        saveTask();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveTask();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        saveTask();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        saveTask();
    }

    @Override
    public void clearTasksByType(String type) {
        super.clearTasksByType(type);
        saveTask();
    }

    @Override
    public void calculateEpicsTime(Epic epic) {
        super.calculateEpicsTime(epic);
        saveTask();
    }
}


