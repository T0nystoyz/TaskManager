package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasks.Status.*;

public class InMemoryTaskManager implements TaskManager {

    public final HistoryManager historyManager;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateNewId() {
        return ++idCounter;
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Ошибка!!! Передана несуществующая задача");
            throw new IllegalArgumentException();
        }

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public int createNewTask(Task task) {
        if (task.getId() != null) {
            System.out.println("Ошибка!!! Передана задача с непустым id");
            throw new IllegalArgumentException();
        }
        if (tasks.containsKey(task.getId())) {
            System.out.println("Ошибка!!! Передана уже существующая задача");
            throw new IllegalArgumentException();
        }

        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            if (!tasks.containsKey(epicId)) {
                System.out.println("Ошибка!!! Передан epicId, которого ещё нет в базе");
                throw new IllegalArgumentException();
            }
            subtask.setId(generateNewId());
            tasks.put(subtask.getId(), subtask);

            Epic epic = (Epic) tasks.get(epicId);
            final List<Integer> subtasksIds = epic.getSubtasksIds();
            subtasksIds.add(subtask.getId());
            updateEpicStatus(epic);
            return subtask.getId();
        } else {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
            return task.getId();
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        final List<Subtask> subtasks = getAllSubtasksOfEpicByEpicId(epic.getId());
        Map<String, Integer> statusCounter = new HashMap<>();
        int allSubtasksCount = 0;
        for (Subtask subtask : subtasks) {
            Integer statusCount = statusCounter.getOrDefault(subtask.getStatus(), 0);
            statusCounter.put(String.valueOf(subtask.getStatus()), statusCount + 1);
            allSubtasksCount += 1;
        }
        if (allSubtasksCount == 0 || statusCounter.getOrDefault(NEW, 0) == allSubtasksCount) {
            epic.setStatus(NEW);
        } else if (statusCounter.getOrDefault(DONE , 0) == allSubtasksCount) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Ошибка!!! Передана задача с пустым id");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Ошибка!!! Передана несуществующая задача");
            return;
        }
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            if (!tasks.containsKey(epicId)) {
                System.out.println("Ошибка!!! Передан epicId, которого ещё нет в базе");
                return;
            }
            tasks.put(subtask.getId(), subtask);

            Epic epic = (Epic) tasks.get(epicId);
            updateEpicStatus(epic);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Ошибка!!! Передана несуществующая задача");
            return;
        }

        Task task = tasks.get(id);
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            Epic epic = (Epic) tasks.get(epicId);
            epic.getSubtasksIds().remove(subtask.getId());
            updateEpicStatus(epic);
        } else if (task.getTaskType().equals("epic")) {
            Epic epic = (Epic) task;
            if (!epic.getSubtasksIds().isEmpty()) {
                List<Integer> subtasksOfEpic = epic.getSubtasksIds();
                for (Integer i : subtasksOfEpic) {
                    tasks.remove(i);
                }
            }
        }
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearTasksByType(String type) {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        for (Task listedTasks : allTasks) {
            if (type.equals("subtask") && listedTasks.getTaskType().equals("subtask")) {
                tasks.remove(listedTasks.getId());
            } else if (type.equals("epic") && listedTasks.getTaskType().equals("epic")) {
                Epic epic = (Epic) listedTasks;
                if (!epic.getSubtasksIds().isEmpty()) {
                    System.out.println("Ошибка!!! Нельзя удалить эпик с подзадачами");
                    return;
                } else {
                    tasks.remove(listedTasks.getId());
                }
            } else if (type.equals("task") && listedTasks.getTaskType().equals("task")) {
                tasks.remove(listedTasks.getId());
            }
        }
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpicByEpicId(int id) {
        Task task = tasks.get(id);
        if (!task.getTaskType().equals("epic")) {
            System.out.println("Ошибка!!! Нельзя получить список подзадач по id задачи или подзадачи");
        }
        Epic epic = (Epic) task;
        List<Integer> subtasksIds = epic.getSubtasksIds();
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : subtasksIds) {
            Subtask subtask = (Subtask) tasks.get(subtaskId);
            subtasks.add(subtask);
        }
        return subtasks;
    }

    @Override
    public List<Task> showAllTasksByType(String type) {
        List<Task> typedTasks = new ArrayList<>();
        for (Task values : tasks.values()) {
            if (values.getTaskType().equals(type)) {
                typedTasks.add(values);
            }
        }
        return typedTasks;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}
