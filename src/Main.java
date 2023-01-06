import model.*;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        Epic epic1 = new Epic("Сходить в магазин","Надо купить продуктов");
        Subtask subtask1 = new Subtask("Купить молоко", "Стоимость 80 рублей",StatusTask.DONE);
        Subtask subtask2 = new Subtask("Купить яица","Стоимость 100 рублей", StatusTask.DONE);
        Epic epic2 = new Epic("Сделать омлет", "Надо приготовить омлет из продкутов");
        Subtask subtask3 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");
        TaskManager manager = Managers.getDefault();

        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.createTask(epic2);
        manager.createTask(subtask3);
        manager.getAllTasks();
        manager.updateTask(epic1);
        manager.updateTask(epic2);
        manager.getAllTasks();


        manager.getTask(0);
        manager.getTask(2);
        manager.getHistory();

        manager.getSubtask(1);
        manager.getEpic(0);
        manager.getHistory();


        manager.getEpic(3);
        manager.getTask(0);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getTask(0);
        manager.getTask(2);
        manager.getSubtask(1);
        manager.getEpic(3);
        manager.getTask(0);
        manager.getTask(2);
        manager.getSubtask(1);
        manager.getEpic(3);
        manager.getHistory();
    }
}
