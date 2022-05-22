package tasks;

import managers.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Epic extends Task {

    private List<Integer> subtasksIds;

    public Epic(String name, String description, List<Integer> subtasksIds) {
        super(name, description, Status.NEW);
        this.subtasksIds = subtasksIds;
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public List<Integer> getSubtasksIds() {

        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public String getTaskType() {
        return "epic";
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        TreeSet<Task> epicsSubtasksWithTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        Subtask subtask;
        if (!this.getSubtasksIds().isEmpty()) {
            for (int id : this.getSubtasksIds()) {
                subtask = (Subtask) InMemoryTaskManager.getTasks().get(id);
                if (subtask.getStartTime() != null) {
                    epicsSubtasksWithTime.add(subtask);
                }
            }
            if (!epicsSubtasksWithTime.isEmpty()) {
                endTime = epicsSubtasksWithTime.last().getEndTime();
            }
        }
        return endTime;
    }

    public void addSubtaskIds(Subtask subtask) {
        subtasksIds.add(subtask.getId());
    }

    @Override
    public String toString() {
        if (getStartTime() == null || getEndTime() == null) {
            return getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," +
                    "null" + "," + getDuration() + "," + "null";
        } else {
            return getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," +
                    getStartTime() + "," + getDuration() + "," + getEndTime();
        }
    }
}
