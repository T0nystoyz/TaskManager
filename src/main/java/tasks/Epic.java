package tasks;

import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task { //поля startTime и Duration высчитываются в менеджерах
    // методом calculateEpicsTime(Epic epic) при создании, удалении и обновлении его подзадач.
    // Метод getEndTime должен высчитывать как в супер классе, исходя из полей duration и startTime.

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
        return super.getEndTime();
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
