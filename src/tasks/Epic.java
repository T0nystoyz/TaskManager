package tasks;

import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasksIds;

    public Epic(String name, String description, List<Integer> subtasksIds) {
        super(name, description, "new");
        this.subtasksIds = subtasksIds;
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


}