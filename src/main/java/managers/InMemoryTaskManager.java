package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;

import static tasks.Status.*;

public class InMemoryTaskManager implements TaskManager, Serializable {

    public static void setTasks(Map<Integer, Task> tasks) {
        InMemoryTaskManager.tasks = tasks;
    }

    protected static Map<Integer, Task> tasks = new HashMap<>();
    protected static TreeSet<Task> sortedTasksByTime =
            new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected static TreeSet<Task> sortedTasksByType =
            new TreeSet<>(Comparator.comparing(Task::getTaskType).thenComparing(Task::getId));
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private int idCounter = 0;

    public InMemoryTaskManager() {
    }

    public static Map<Integer, Task> getTasks() {
        return tasks;
    }

    public InMemoryHistoryManager getHistoryManager() {
        return historyManager;
    }

    /**
     * method that combines two treeSets
     *
     * @return a list of tasks in natural temporal order
     */
    public List<Task> getPrioritizedTasks() {
        ArrayList<Task> prioritizedTasks = new ArrayList<>(sortedTasksByTime);
        prioritizedTasks.addAll(sortedTasksByType);
        return prioritizedTasks;
    }

    private boolean checkForIntersection(Task task1, Task task2) {
        if (task1.getStartTime() != null && task1.getEndTime() != null && task2.getStartTime() != null && task2.getEndTime() != null &&
                (!(task1 instanceof Epic)) && (!(task2 instanceof Epic))) {

            return task1.getStartTime().isBefore(task2.getStartTime()) && task2.getEndTime().isAfter(task2.getStartTime()) ||
                    task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getEndTime()) ||
                    task1.getStartTime().isBefore(task2.getStartTime()) && task1.getEndTime().isAfter(task2.getEndTime()) ||
                    task1.getStartTime().isAfter(task2.getStartTime()) && task1.getEndTime().isBefore(task2.getEndTime());
        } else {
            return false;
        }
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
        Optional<Task> intersectedTask = getAllTasks().stream()
                .filter(existingTask -> checkForIntersection(task, existingTask))
                .findFirst();

        if (intersectedTask.isPresent()) {
            throw new IllegalArgumentException("Новая задача пересекается по времени с другой существующей задачей: "
                    + intersectedTask.get().getId());
        }

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
            if (task.getStartTime() != null) {
                sortedTasksByTime.add(task);
            } else {
                sortedTasksByType.add(task);
            }

            Epic epic = (Epic) tasks.get(epicId);
            final List<Integer> subtasksIds = epic.getSubtasksIds();
            subtasksIds.add(subtask.getId());
            updateEpicStatus(epic);
            calculateEpicsTime(epic);
            return subtask.getId();
        } else {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                sortedTasksByTime.add(task);
            } else {
                sortedTasksByType.add(task);
            }
            return task.getId();
        }
    }

    @Override
    public void clearHistory() {
        historyManager.clearHistory();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        final List<Subtask> subtasks = getAllSubtasksOfEpicByEpicId(epic.getId());
        Map<Status, Integer> statusCounter = new HashMap<>();
        int allSubtasksCount = 0;
        for (Subtask subtask : subtasks) {
            Integer statusCount = statusCounter.getOrDefault(subtask.getStatus(), 0);
            statusCounter.put(Status.valueOf(String.valueOf(subtask.getStatus())), statusCount + 1);
            allSubtasksCount += 1;
        }
        if (allSubtasksCount == 0 || statusCounter.getOrDefault(NEW, 0) == allSubtasksCount) {
            epic.setStatus(NEW);
        } else if (statusCounter.getOrDefault(DONE, 0) == allSubtasksCount) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Ошибка!!! Передана задача с пустым id");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Ошибка!!! Передана несуществующая задача");
        }
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            if (!tasks.containsKey(epicId)) {
                throw new IllegalArgumentException("Ошибка!!! Передан epicId, которого ещё нет в базе");
            }
            tasks.put(subtask.getId(), subtask);

            Epic epic = (Epic) tasks.get(epicId);
            updateEpicStatus(epic);
            calculateEpicsTime(epic);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public String removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Ошибка!!! Передана несуществующая задача");
        }
        Task task = tasks.get(id);
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            Epic epic = (Epic) tasks.get(epicId);
            epic.getSubtasksIds().remove(subtask.getId());
            updateEpicStatus(epic);
            calculateEpicsTime(epic);
        } else if (task.getTaskType().equals("epic")) {
            Epic epic = (Epic) task;
            if (!epic.getSubtasksIds().isEmpty()) {
                List<Integer> subtasksOfEpic = epic.getSubtasksIds();
                for (Integer i : subtasksOfEpic) {
                    tasks.remove(i);
                    historyManager.remove(i);
                }
            }
        }
        tasks.remove(id);
        historyManager.remove(id);
        return "task has been removed";
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearTasksByType(String type) {
        type = type.toLowerCase().trim();
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        for (Task listedTasks : allTasks) {
            if (listedTasks.getTaskType().equals(type)) {
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
            } else {
                System.out.println("Задач с таким типом нет!");
            }
        }
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpicByEpicId(int id) {
        Task task = tasks.get(id);
        if (!task.getTaskType().equals("epic")) {
            throw new IllegalArgumentException("Ошибка!!! Нельзя получить список подзадач по id задачи или подзадачи");
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
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> showAllTasksByType(String type) {
        type = type.toLowerCase().trim();
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

    @Override
    public void calculateEpicsTime(Epic epic) {
        TreeSet<Task> epicsSubtasksWithTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        Subtask subtask;
        if (!epic.getSubtasksIds().isEmpty()) {
            for (int id : epic.getSubtasksIds()) {
                subtask = (Subtask) tasks.get(id);
                if (subtask.getStartTime() != null) {
                    epicsSubtasksWithTime.add(subtask);
                }
            }
            if (!epicsSubtasksWithTime.isEmpty()) {
                Duration duration = Duration.between(epicsSubtasksWithTime.first().getStartTime(), epicsSubtasksWithTime.last().getEndTime());
                int durationEpicTask = (int) duration.toMinutes();
                epic.setStartTime(epicsSubtasksWithTime.first().getStartTime());
                epic.setDuration(Duration.ofMinutes(durationEpicTask));
            }
        } else {
            epic.setStartTime(null);
            epic.setDuration(Duration.ofMinutes(0));
        }
    }
}
