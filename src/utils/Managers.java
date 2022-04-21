package utils;

import managers.*;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFromCSV(File file) {
        return new FileBackedTasksManager(file);
    }
}