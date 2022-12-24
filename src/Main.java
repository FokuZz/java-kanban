import manager.taskManager.TaskManager;
import taskTracker.Epic;
import taskTracker.Subtask;

public class Main {
    public static void main(String[] args) {
        Epic epic1 = new Epic("Сходить в магазин","Надо купить продуктов");
        Subtask subtask1 = new Subtask("Купить молоко", "Стоимость 80 рублей", "DONE");
        Subtask subtask2 = new Subtask("Купить яица","Стоимость 100 рублей", "DONE");
        Epic epic2 = new Epic("Сделать омлет", "Надо приготовить омлет из продкутов");
        Subtask subtask3 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");
        TaskManager manager = new TaskManager();

        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.createTask(epic2);
        manager.createTask(subtask3);
        manager.getAllTasks();
        manager.updateTask(subtask3);
        manager.updateTask(epic2);
        manager.deleteAllById(0);
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.getById(2);
        manager.getAllSubtaskEpic(epic1);
        manager.getAllTasks();
    }
}
