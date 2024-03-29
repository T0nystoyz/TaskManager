package taskManagers;

import managers.FileBackedTasksManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    protected void setManager() {
        this.taskManager = new FileBackedTasksManager();
    }

    @DisplayName ("GIVEN an instance of empty manager " +
            "WHEN loaded from empty file " +
            "THEN return empty manager")
    @Test
    public void test1_loadEmptyTaskManager() {
        FileBackedTasksManager loaded = FileBackedTasksManager.loadFromFile(new File("./src/file.csv"));
        assertTrue(loaded.getAllTasks().isEmpty());
    }

    @DisplayName ("GIVEN an instance of epic without subtasks " +
            "WHEN save " +
            "THEN return manager with empty epic")
    @Test
    public void test2_loadTaskManagerWithEmptyEpic() {
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", new ArrayList<>()));
        FileBackedTasksManager loaded = FileBackedTasksManager.loadFromFile(new File("./src/file.csv"));
        Epic epic = (Epic) loaded.getTaskById(1);
        assertTrue(epic.getSubtasksIds().isEmpty());
    }

    @DisplayName ("GIVEN an instance empty history " +
            "WHEN save and load " +
            "THEN return manager with empty history")
    @Test
    public void test3_loadTaskManagerWithEmptyHistory() {
        taskManager.saveHistory(taskManager.getHistoryManager());
        FileBackedTasksManager loaded = FileBackedTasksManager.loadFromFile(new File("./src/file.csv"));
        assertTrue(loaded.history().isEmpty());
    }


}