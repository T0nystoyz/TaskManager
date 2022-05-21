package tasks;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {
    private Integer id;
    private final String name;
    private final String description;
    private Status status;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTaskType() {
        return "task";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        String result;
        try {
            String q = String.valueOf(getStartTime());
            String w = String.valueOf(getEndTime());
            result = getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," +
                    q + "," + getDuration() + "," + w;
        } catch (NullPointerException ex) {
            String q = String.valueOf(getStartTime());
            String w = "null";
            result = getId() + "," + getTaskType() + "," + getName() +
                    "," + getStatus() + "," + getDescription() + "," +
                    q + "," + getDuration() + "," + w;
        }

        return result;

    }

}
