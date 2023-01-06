package model;

public class Subtask extends Task{
    private int subTaskId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, StatusTask status) {
        super(name, description);
        setStatus(status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", subTaskId=" + subTaskId +
                ", id=" + getSuperId() +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    public void setSubtaskId(int id) {
        this.subTaskId = id;
    }

    public int getSubtaskId() {
        return subTaskId;
    }
}
