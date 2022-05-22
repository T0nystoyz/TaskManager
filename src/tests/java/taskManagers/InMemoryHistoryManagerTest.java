package taskManagers;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.NEW;

class InMemoryHistoryManagerTest {
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @AfterEach
    void clear() {
        taskManager.clearAllTasks();
        taskManager.clearHistory();
    }

    @DisplayName ("GIVEN a new instance of map of tasks " +
            "WHEN a new getTask() method call " +
            "THEN add to history")
    @Test
    void test1_shouldAddTaskToHistory() {
        int task1Id = taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        assertEquals(1, taskManager.getHistoryManager().getHistory().size());
    }

    @DisplayName ("GIVEN a new instance of empty map of tasks " +
            "WHEN call history() method " +
            "THEN return empty history")
    @Test
    void test1_1_shouldReturnEmptyHistory() {
        assertTrue(taskManager.history().isEmpty());
    }

    @DisplayName ("GIVEN a new instance of map of tasks " +
            "WHEN a getTask() method call on the same task " +
            "THEN return history without doubling results")
    @Test
    void test1_2_shouldReturnHistoryOfLastCallsWithoutDouble() {
        int task1Id = taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task1Id);
        assertEquals(1, taskManager.history().size());
    }

    @DisplayName ("GIVEN a new instance of history " +
            "WHEN a remove() method call on first element " +
            "THEN remove")
    @Test
    void test2_remove() {
        int task1Id = taskManager.createNewTask(new Task("Задача 11", "Просто текст", NEW));
        int task2Id = taskManager.createNewTask(new Task("Задача 22", "Просто текст", NEW));
        int task3Id = taskManager.createNewTask(new Task("Задача 33", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task3Id);
        taskManager.getHistoryManager().remove(task1Id);
        assertEquals(2, taskManager.history().size());
        assertEquals(2,taskManager.history().get(0).getId());
    }

    @DisplayName ("GIVEN a new instance of history " +
            "WHEN a remove() method call on last element " +
            "THEN remove")
    @Test
    void test2_1_remove() {
        int task1Id = taskManager.createNewTask(new Task("Задача 111", "Просто текст", NEW));
        int task2Id = taskManager.createNewTask(new Task("Задача 222", "Просто текст", NEW));
        int task3Id = taskManager.createNewTask(new Task("Задача 333", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task3Id);
        taskManager.getHistoryManager().remove(task3Id);
        assertEquals(2, taskManager.history().size());
        assertFalse(taskManager.history().contains(taskManager.getTaskById(task3Id)));
    }

    @DisplayName ("GIVEN a new instance of history " +
            "WHEN a remove() method call on middle element " +
            "THEN remove")
    @Test
    void test2_2_remove() {
        int task1Id = taskManager.createNewTask(new Task("Задача 01", "Просто текст", NEW));
        int task2Id = taskManager.createNewTask(new Task("Задача 02", "Просто текст", NEW));
        int task3Id = taskManager.createNewTask(new Task("Задача 03", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task3Id);
        taskManager.getHistoryManager().remove(task2Id);
        assertEquals(2, taskManager.history().size());
        assertFalse(taskManager.history().contains(taskManager.getTaskById(task2Id)));
    }

    @DisplayName ("GIVEN a new instance of history " +
            "WHEN a clearHistory() method call " +
            "THEN clear history")
    @Test
    void test3_clearHistory() {
        int task1Id = taskManager.createNewTask(new Task("Задача 001", "Просто текст", NEW));
        int task2Id = taskManager.createNewTask(new Task("Задача 002", "Просто текст", NEW));
        int task3Id = taskManager.createNewTask(new Task("Задача 003", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task3Id);
        taskManager.clearHistory();
        assertTrue(taskManager.history().isEmpty());
    }

    @DisplayName ("GIVEN a new instance of history " +
            "WHEN a getHistory() method call " +
            "THEN return history via list of tasks")
    @Test
    void test4_getHistory() {
        int task1Id = taskManager.createNewTask(new Task("Задача 0001", "Просто текст", NEW));
        int task2Id = taskManager.createNewTask(new Task("Задача 0002", "Просто текст", NEW));
        int task3Id = taskManager.createNewTask(new Task("Задача 0003", "Просто текст", NEW));
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task3Id);
        assertFalse(taskManager.history().isEmpty());
        assertEquals(3, taskManager.history().size());
    }
}