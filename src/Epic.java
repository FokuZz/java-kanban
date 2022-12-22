public class Epic extends Task{
    private int epicId;
    private int amountSubtasks;
    Epic(String name, String description) { super(name, description); }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", epicId=" + epicId +
                ", amountSubtasks=" + amountSubtasks +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        this.epicId = id;
    }

    @Override
    public int getId() {
        return epicId;
    }

    public int getAmountSubtasks() {
        return amountSubtasks;
    }

    public void addAmountSubtasks() {
        this.amountSubtasks++;
    }

}
