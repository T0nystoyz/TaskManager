package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    int generateNewId();

    Task getTaskById(int id);

    int createNewTask(Task task);

    void clearHistory();

    void updateEpicStatus(Epic epic);

    void updateTask(Task task);

    String removeTaskById(int id);

    void clearAllTasks();

    void clearTasksByType(String type);

    List<Subtask> getAllSubtasksOfEpicByEpicId(int id);

    List<Task> getAllTasks();

    List<Task> showAllTasksByType(String type);

    void calculateEpicsTime(Epic epic);

    List<Task> history();
}
