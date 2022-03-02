package Tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(Integer id, String name, String description, String status) {
        super(id, name, description, status);
    }

    public Subtask(String name, String description, String status) {
        super(name, description, status);
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
}
