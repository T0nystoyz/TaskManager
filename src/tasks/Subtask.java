package tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(Integer id, String name, Status status, String description,  int epicId) {
        super(id, name, description, status);
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
        return getId() + "," + getTaskType() + "," + getName() + "," + getStatus() + "," + getDescription()
                + "," + getEpicId();
    }
}
