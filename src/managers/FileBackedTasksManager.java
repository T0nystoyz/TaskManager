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
import java.util.ArrayList;
import java.util.List;

import static tasks.Status.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private File file = new File("C:\\Users\\Admin\\IdeaProjects\\java-sprint2-hw\\src\\file.csv");

    public FileBackedTasksManager(File file) {
        setFile(file);
    }

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
                        Status.valueOf(lin[3]),
                        lin[4],
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

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(String.valueOf(file)));
            for (String line : lines) {
                extractTask(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskManager;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public int createNewTask(Task task) {
        saveTask();
        return super.createNewTask(task);
    }

    private void saveTask() {
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
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Неудачная попытка автосохранения");
            } catch (ManagerSaveException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveHistory(InMemoryHistoryManager hm) {
        String history = hm.toString();
        try (Writer writer = new FileWriter(file, true)) {
            writer.write("\n");
            writer.write(history);
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Неудачная попытка автосохранения");
            } catch (ManagerSaveException ex) {
                ex.printStackTrace();
            }
        }
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

    public static class Main {
        public static void main(String[] args) {

            InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
            FileBackedTasksManager fb = new FileBackedTasksManager();

            Task task = new Task("Задача 1", "Просто текст", NEW);
            int task1Id = fb.createNewTask(task);
            Task updatedTask = new Task(task1Id, "Задача 1", "Просто текст", IN_PROGRESS);
            fb.updateTask(updatedTask);

            Task task2 = new Task("Задача 2", "Просто текст", NEW);
            int task2Id = fb.createNewTask(task2);

            List<Integer> epics1Subtasks = new ArrayList<>();
            Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
            int epicId = fb.createNewTask(epic1);

            Subtask subTask1 = new Subtask("Подзадача №1 первого эпика", "текст", NEW);
            subTask1.setEpicId(epicId);
            int subTask1Id = fb.createNewTask(subTask1);
            epic1.addSubtaskIds(subTask1);

            Subtask subTask2 = new Subtask("Подзадача №2 первого эпика", "текст", NEW);
            subTask2.setEpicId(epicId);
            int subTask2Id = fb.createNewTask(subTask2);
            epic1.addSubtaskIds(subTask2);

            List<Integer> epics2Subtasks = new ArrayList<>();
            Epic epic2 = new Epic("Эпик 2", "текст", epics2Subtasks);
            int epic2Id = fb.createNewTask(epic2);

            Subtask subTask3 = new Subtask("Подзадача второго эпика", "текст", NEW);
            subTask3.setEpicId(epic2Id);
            int subTask3Id = fb.createNewTask(subTask3);

            Subtask updatedSubTask3 = new Subtask(subTask3Id, "Завершенная подзадача второго эпика", "текст", DONE);
            updatedSubTask3.setEpicId(epic2Id);
            fb.updateTask(updatedSubTask3);

            fb.updateEpicStatus(epic2);
            fb.getTaskById(1);
            fb.getTaskById(2);
            fb.getTaskById(3);
            fb.getTaskById(4);
            fb.getTaskById(2);
            fb.saveHistory(historyManager);


            FileBackedTasksManager fbSaved = FileBackedTasksManager.loadFromFile(fb.getFile());
            System.out.println(fbSaved.getTaskById(1));
            System.out.println(fbSaved.getTaskById(2));
            System.out.println(fbSaved.getTaskById(3));
            System.out.println(fbSaved.getTaskById(1));
            System.out.println(historyManager);
        }
    }
}


