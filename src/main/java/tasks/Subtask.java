package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(Integer id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String getTaskType() {
        return "subtask";
    }

    @Override
    public String toString() {
        if (getStartTime() == null || getEndTime() == null) {
            return getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," + getEpicId() +
                    "null" + "," + getDuration() + "," + "null";
        } else {
            return getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," + getEpicId() +
                    getStartTime() + "," + getDuration() + "," + getEndTime();
        }
    }
}
