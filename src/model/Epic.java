package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task{
    private int epicId;
    private int amountSubtasks;
    private LocalDateTime startTime;
    private Duration duration;
    public Epic(String name, String description) { super(name, description); }

    public Epic(String name, String description, LocalDateTime startTime, int durationInMinutes){
        super(name,description);
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationInMinutes);
    }

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

    public LocalDateTime getEndTime() { return startTime.plus(duration);}

    public boolean isTaskCopyTime(Epic task) {
        return this.startTime.equals(task.startTime);
    }

}
