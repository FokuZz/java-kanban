package taskTracker;

public class Subtask extends Task{
    private int subTaskId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, String status) {
        super(name, description);
        setStatus(status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", subTaskId=" + subTaskId +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        this.subTaskId = id;
    }

    @Override
    public int getId() {
        return subTaskId;
    }
}
