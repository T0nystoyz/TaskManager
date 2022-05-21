package taskManagers;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.Status.*;

class InMemoryTaskManagerTest {
    final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @AfterEach
    void clear() {
        taskManager.clearAllTasks();
    }

    @Test
    void getPrioritizedTasks() {
    }

    @DisplayName ("GIVEN a new instance of Task " + "WHEN a new task created " + "THEN returned id counter++")
    @Test
    void test1_generateNewIdShouldIncrementIdCounter() {
        Task task = new Task("Задача 1", "Просто текст", NEW);
        taskManager.createNewTask(task);
        assertEquals(1, task.getId());
    }

    @DisplayName ("GIVEN a instance of HashMap of tasks " + "WHEN calling Task by id " + "THEN returned Task with that id")
    @Test
    void test2_getTaskByIdShouldReturnCorrespondingTaskById() {
        Task task = new Task("Задача 1", "Просто текст", NEW);
        taskManager.createNewTask(task);
        assertEquals(task, taskManager.getTaskById(1));
    }

    @DisplayName ("GIVEN an instance of HashMap of tasks " + "WHEN calling Task by non-existent id " + "THEN throw IllegalArgumentException")
    @Test
    void test2_1_getTaskByWrongIdShouldThrowIllegalArgumentException() {
        Task task = new Task("Задача 1", "Просто текст", NEW);
        taskManager.createNewTask(task);

        assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(2));
    }

    @DisplayName ("GIVEN a instance of empty HashMap " + "WHEN calling Task by id " + "THEN throw IllegalArgumentException")
    @Test
    void test2_2_getTaskByIdFromEmptyMapShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.getTaskById(1));
    }


    @DisplayName ("GIVEN a new instance of Task " +
            "WHEN Task intersects with another Task " +
            "THEN throw IllegalArgumentException")
    @Test
    void test3_createNewTaskWithIntersectionShouldThrowIllegalArgumentException() {
        taskManager.createNewTask(new Task("Задача 1", "текст", NEW,
                LocalDateTime.parse("2022-04-27 10:15:30", formatter),
                Duration.of(10, ChronoUnit.MINUTES)));
        assertThrows(IllegalArgumentException.class, () -> taskManager.createNewTask(new Task("Задача 2", "текст", NEW,
                LocalDateTime.parse("2022-04-27 10:20:30", formatter),
                Duration.of(10, ChronoUnit.MINUTES))));
    }

    @DisplayName ("GIVEN a new instance of Task " +
            "WHEN Task is created " +
            "THEN return id")
    @Test
    void test3_1_createNewTaskWithoutIntersectionShouldReturnId() {
        int id = taskManager.createNewTask(new Task("Задача 1", "текст", NEW));
        assertEquals(1, id);
    }

    @DisplayName ("GIVEN a new instance of Task with already given id " +
            "WHEN Task is created " +
            "THEN throw IllegalArgumentException")
    @Test
    void test3_2_createNewTaskWithNonNullIdShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.createNewTask(new Task(1, "Задача 2", "текст", NEW)));
    }

    @DisplayName ("GIVEN a new instance of Subtask " +
            "WHEN Subtask is created " +
            "THEN setting Subtask's EpicId")
    @Test
    void test3_3_createNewTaskWithNonNullIdShouldThrowIllegalArgumentException() {
        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        int epicId = taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("Подзадача №1 первого эпика", "текст", NEW);
        subTask1.setEpicId(epicId);
        taskManager.createNewTask(subTask1);
        epic1.addSubtaskIds(subTask1);
        assertEquals(epic1.getId(), subTask1.getEpicId());
    }

    @DisplayName ("GIVEN a new instance of two Subtasks, one is NEW, another DONE " +
            "WHEN calculate Epics status " +
            "THEN Epics status equals IN_PROGRESS")
    @Test
    void test4_updateEpicStatusShouldCalculateFromSubtasksStatuses() {
        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("1 подзадача второго эпика", "текст", DONE, 1);
        int subTask1Id = taskManager.createNewTask(subTask1);
        epics1Subtasks.add(subTask1Id);

        Subtask subTask2 = new Subtask("2 подзадача второго эпика", "текст", NEW, 1);
        int subTask2Id = taskManager.createNewTask(subTask2);
        epics1Subtasks.add(subTask2Id);
        epic1.setSubtasksIds(epics1Subtasks);
        taskManager.updateEpicStatus(epic1);
        assertEquals(IN_PROGRESS, epic1.getStatus());
    }

    @DisplayName ("GIVEN a new instance of two Subtasks, both are NEW " +
            "WHEN calculate Epics status " +
            "THEN Epics status equals NEW")
    @Test
    void test4_1_updateEpicStatusShouldCalculateFromSubtasksStatuses() {
        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("1 подзадача второго эпика", "текст", NEW, 1);
        int subTask1Id = taskManager.createNewTask(subTask1);
        epics1Subtasks.add(subTask1Id);

        Subtask subTask2 = new Subtask("2 подзадача второго эпика", "текст", NEW, 1);
        int subTask2Id = taskManager.createNewTask(subTask2);
        epics1Subtasks.add(subTask2Id);
        epic1.setSubtasksIds(epics1Subtasks);
        taskManager.updateEpicStatus(epic1);
        assertEquals(NEW, epic1.getStatus());
    }

    @DisplayName ("GIVEN a new instance of two Subtasks, both are DONE " +
            "WHEN calculate Epics status " +
            "THEN Epics status equals DONE")
    @Test
    void test4_2_updateEpicStatusShouldCalculateFromSubtasksStatuses() {
        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("1 подзадача второго эпика", "текст", DONE, 1);
        int subTask1Id = taskManager.createNewTask(subTask1);
        epics1Subtasks.add(subTask1Id);

        Subtask subTask2 = new Subtask("2 подзадача второго эпика", "текст", DONE, 1);
        int subTask2Id = taskManager.createNewTask(subTask2);
        epics1Subtasks.add(subTask2Id);
        epic1.setSubtasksIds(epics1Subtasks);
        taskManager.updateEpicStatus(epic1);
        assertEquals(DONE, epic1.getStatus());
    }

    @DisplayName ("GIVEN a new instance of two Subtasks, both are IN_PROGRESS " +
            "WHEN calculate Epics status " +
            "THEN Epics status equals IN_PROGRESS")
    @Test
    void test4_3_updateEpicStatusShouldCalculateFromSubtasksStatuses() {
        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("1 подзадача второго эпика", "текст", IN_PROGRESS, 1);
        int subTask1Id = taskManager.createNewTask(subTask1);
        epics1Subtasks.add(subTask1Id);

        Subtask subTask2 = new Subtask("2 подзадача второго эпика", "текст", IN_PROGRESS, 1);
        int subTask2Id = taskManager.createNewTask(subTask2);
        epics1Subtasks.add(subTask2Id);
        epic1.setSubtasksIds(epics1Subtasks);
        taskManager.updateEpicStatus(epic1);
        assertEquals(IN_PROGRESS, epic1.getStatus());
    }

    @DisplayName ("GIVEN a new instance of Task " +
            "WHEN update Task " +
            "THEN Task is updated (old replaced with new)")
    @Test
    void test5_updateTask() {
        Task task = new Task("Задача 1", "Просто текст", NEW);
        int task1Id = taskManager.createNewTask(task);
        Task updatedTask = new Task(task1Id, "Задача 1", "Просто текст", IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        assertEquals(taskManager.getTaskById(task1Id), updatedTask);
    }

    @DisplayName ("GIVEN a new instance of Task " +
            "WHEN update Task with inappropriate (null or non-existent) id " +
            "THEN throw IllegalArgumentException")
    @Test
    void test5_1_updateTaskShouldThrowIllegalArgumentException() {
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(new Task(4, "Задача 1", "Просто текст", IN_PROGRESS)));
        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(new Task("Задача 1", "Просто текст", IN_PROGRESS)));
    }

    @DisplayName ("GIVEN a new instance of two HashMaps " +
            "WHEN delete Task by id " +
            "THEN two maps are equal")
    @Test
    void test6_removeTaskByIdShouldRemove() {
        Map<Integer, Task> expectedMap = new HashMap<>();
        expectedMap.put(1, new Task(1, "Задача 1", "Просто текст", NEW));
        expectedMap.put(2, new Task(2, "Задача 2", "Просто текст", NEW));
        expectedMap.remove(1);
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.createNewTask(new Task("Задача 2", "Просто текст", NEW));
        taskManager.removeTaskById(1);
        assertEquals(expectedMap, InMemoryTaskManager.getTasks());
    }

    @DisplayName ("GIVEN an empty map " +
            "WHEN delete Task by id " +
            "THEN throw IllegalArgumentException")
    @Test
    void test6_1_removeTaskByIdInEmptyMap() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.removeTaskById(1));
    }

    @DisplayName ("GIVEN an empty map " +
            "WHEN delete Task by id " +
            "THEN throw IllegalArgumentException")
    @Test
    void test6_2_removeTaskByWrongId() {
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        assertThrows(IllegalArgumentException.class, () -> taskManager.removeTaskById(2));
    }

    @DisplayName ("GIVEN a map of tasks/empty map " +
            "WHEN delete all tasks " +
            "THEN there is empty map")
    @Test
    void test7_clearAllTasks() {
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.createNewTask(new Task("Задача 2", "Просто текст", NEW));
        taskManager.clearAllTasks();
        assertTrue(InMemoryTaskManager.getTasks().isEmpty());
    }

    @DisplayName ("GIVEN a map of tasks " +
            "WHEN delete all tasks by type " +
            "THEN map does not contain tasks of that type")
    @Test
    void test8_clearTasksByType() {
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", new ArrayList<>()));
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        taskManager.createNewTask(new Task("Задача 2", "Просто текст", NEW));
        taskManager.clearTasksByType("task");
        assertTrue(taskManager.showAllTasksByType("task").isEmpty());
    }

    @DisplayName ("GIVEN an Epic with Subtask " +
            "WHEN delete Epic " +
            "THEN can not delete Epic")
    @Test
    void test8_1_clearEpicWithSubtask() {
        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(2);
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", subtasks));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        taskManager.clearTasksByType("epic");
        assertFalse(taskManager.showAllTasksByType("epic").isEmpty());
    }

    @DisplayName ("GIVEN an Epic with Subtasks " +
            "WHEN get all epics subtasks " +
            "THEN get list of Subtasks")
    @Test
    void test9_getAllSubtasksOfEpicByEpicId() {
        ArrayList<Integer> subtasks = new ArrayList<>();
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", subtasks));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        taskManager.createNewTask(new Subtask("Подзадача 2", "Просто текст", NEW, 1));
        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        Subtask subtask1 = new Subtask(2, "Подзадача", "Просто текст", NEW, 1);
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Просто текст", NEW, 1);
        expectedSubtasks.add(subtask1);
        expectedSubtasks.add(subtask2);
        taskManager.getAllSubtasksOfEpicByEpicId(1);
        assertEquals(expectedSubtasks, taskManager.getAllSubtasksOfEpicByEpicId(1));
    }

    @DisplayName ("GIVEN an Epic with Subtasks " +
            "WHEN get all epics subtasks by task's/subtask's id " +
            "THEN throw IllegalArgumentException")
    @Test
    void test9_1_getAllSubtasksOfEpicByNotEpicId() {
        ArrayList<Integer> subtasks = new ArrayList<>();
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", subtasks));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        taskManager.createNewTask(new Subtask("Подзадача 2", "Просто текст", NEW, 1));
        assertThrows(IllegalArgumentException.class, () -> taskManager.getAllSubtasksOfEpicByEpicId(2));
    }

    @DisplayName ("GIVEN a list of tasks " +
            "WHEN get all tasks " +
            "THEN return all tasks")
    @Test
    void test10_getAllTasks() {
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", new ArrayList<>()));
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        List<Task> expected = new ArrayList<>();
        expected.add(new Epic(1, "Эпик", "Просто текст", NEW));
        expected.add(new Task(2, "Задача 1", "Просто текст", NEW));
        expected.add(new Subtask(3, "Подзадача", "Просто текст", NEW, 1));
        assertEquals(expected, taskManager.getAllTasks());
    }

    @DisplayName ("GIVEN an empty map of tasks " +
            "WHEN get all tasks " +
            "THEN return empty map")
    @Test
    void test10_1_getAllTasksFromEmptyList() {
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @DisplayName ("GIVEN a map of tasks " +
            "WHEN get all tasks by type " +
            "THEN return list of tasks by type ")
    @Test
    void test11_showAllTasksByType() {
        taskManager.createNewTask(new Epic("Эпик", "Просто текст", new ArrayList<>()));
        taskManager.createNewTask(new Task("Задача 1", "Просто текст", NEW));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1));
        List<Task> expected = new ArrayList<>();
        expected.add(new Epic(1, "Эпик", "Просто текст", NEW));
        assertEquals(expected, taskManager.showAllTasksByType("epic"));
    }

    @DisplayName ("GIVEN an empty map of tasks " +
            "WHEN get all tasks by type " +
            "THEN return empty list ")
    @Test
    void test11_1_showAllTasksByTypeInEmptyMap() {
        assertTrue(taskManager.showAllTasksByType("epic").isEmpty());
    }

    @DisplayName ("GIVEN a new instance of epic " +
            "WHEN calculate epic time " +
            "THEN set epic start, duration and end time ")
    @Test
    void test12_calculateEpicsTime() {
        ArrayList<Integer> subtasks = new ArrayList<>();
        int epicId = taskManager.createNewTask(new Epic("Эпик", "Просто текст", subtasks));
        taskManager.createNewTask(new Subtask("Подзадача", "Просто текст", NEW, 1,
                LocalDateTime.parse("2022-04-27 10:20:00", formatter),
                Duration.of(10, ChronoUnit.MINUTES)));
        taskManager.createNewTask(new Subtask("Подзадача 2", "Просто текст", NEW, 1,
                LocalDateTime.parse("2022-04-27 10:40:00", formatter),
                Duration.of(10, ChronoUnit.MINUTES)));
        taskManager.calculateEpicsTime((Epic) taskManager.getTaskById(epicId));

        assertEquals(LocalDateTime.parse("2022-04-27 10:20:00", formatter),
                taskManager.getTaskById(epicId).getStartTime());
        assertEquals(LocalDateTime.parse("2022-04-27 10:50:00", formatter),
                taskManager.getTaskById(epicId).getEndTime());
        assertEquals(Duration.of(30, ChronoUnit.MINUTES),
                taskManager.getTaskById(epicId).getDuration());
    }
}