import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

import static tasks.Status.*;

public class Main {
    public static void main(String[] args) {

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

        Task task = new Task("Задача 1", "Просто текст", NEW);
        int task1Id = taskManager.createNewTask(task);
        Task updatedTask = new Task(task1Id, "Задача 1", "Просто текст", IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task task2 = new Task("Задача 2", "Просто текст", NEW);
        int task2Id = taskManager.createNewTask(task2);

        List<Integer> epics1Subtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "текст", epics1Subtasks);
        int epicId = taskManager.createNewTask(epic1);

        Subtask subTask1 = new Subtask("Подзадача №1 первого эпика", "текст", NEW);
        subTask1.setEpicId(epicId);
        int subTask1Id = taskManager.createNewTask(subTask1);
        epic1.addSubtaskIds(subTask1);

        Subtask subTask2 = new Subtask("Подзадача №2 первого эпика", "текст", NEW);
        subTask2.setEpicId(epicId);
        int subTask2Id = taskManager.createNewTask(subTask2);
        epic1.addSubtaskIds(subTask2);

        List<Integer> epics2Subtasks = new ArrayList<>();
        Epic epic2 = new Epic("Эпик 2", "текст", epics2Subtasks);
        int epic2Id = taskManager.createNewTask(epic2);

        Subtask subTask3 = new Subtask("Подзадача второго эпика", "текст", NEW);
        subTask3.setEpicId(epic2Id);
        int subTask3Id = taskManager.createNewTask(subTask3);

        Subtask updatedSubTask3 = new Subtask(subTask3Id, "Завершенная подзадача второго эпика", "текст", DONE);
        updatedSubTask3.setEpicId(epic2Id);
        taskManager.updateTask(updatedSubTask3);

        taskManager.updateEpicStatus(epic2);

        System.out.println(task);
        System.out.println(updatedTask);
        //taskManager.removeTaskById(task.getId());
        System.out.println(task2);
        //taskManager.clearTasksByType("task");
        taskManager.showAllTasksByType("epic");
        System.out.println(epic1);
        System.out.println(subTask1);
        System.out.println(subTask2);
        System.out.println("Подзадачи первого эпика = " + epics1Subtasks);
        System.out.println(epic2);
        System.out.println(subTask3);
        System.out.println("Подзадачи второго эпика = " + epics2Subtasks);
        System.out.println(updatedSubTask3);
        System.out.println("Статус эпика №2 " + epic2.getStatus());
        System.out.println(taskManager.getAllSubtasksOfEpicByEpicId(3));
        System.out.println(taskManager.getTaskById(6));
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(4);
        taskManager.getTaskById(4);
        taskManager.getTaskById(1);
        System.out.println(taskManager.showAllTasksByType("epic"));
        System.out.println(historyManager.getHistory());


    }
}
