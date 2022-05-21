package tasks;

import java.util.List;
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


    public void addSubtaskIds(Subtask subtask) {
        subtasksIds.add(subtask.getId());
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
