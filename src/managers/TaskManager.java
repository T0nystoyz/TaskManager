package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;


public interface TaskManager {

    int generateNewId();

    Task getTaskById(int id);

    int createNewTask(Task task);

    void updateEpicStatus(Epic epic);

    void updateTask(Task task);

    void removeTaskById(int id);

    void clearAllTasks();

    void clearTasksByType(String type);

    List<Subtask> getAllSubtasksOfEpicByEpicId(int id);

    List<Task> showAllTasksByType(String type);

    List<Task> history();
}
