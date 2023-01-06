package model;

public class Epic extends Task{
    private int epicId;
    private int amountSubtasks;
    public Epic(String name, String description) { super(name, description); }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", epicId=" + epicId +
                ", id=" + getSuperId() +
                ", amountSubtasks=" + amountSubtasks +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    public void setEpicId(int id) {
        this.epicId = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public int getAmountSubtasks() {
        return amountSubtasks;
    }

    public void addAmountSubtasks() {
        this.amountSubtasks++;
    }

}
