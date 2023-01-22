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
        System.out.println("Это до обновления статуса^\n");
        manager.updateTask(epic1);
        manager.updateTask(epic2);
        manager.getAllTasks();
        System.out.println("Это после обновления статуса^\n");
        manager.getTask(0);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getTask(3);
        manager.getTask(4);
        manager.getHistory();
        System.out.println("Это история с 4 разными вызовами^\n");
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getTask(4);
        manager.getTask(3);
        manager.getHistory();
        System.out.println("После множества повторных вызовов Get^\n");
        manager.removeHistory(4);
        manager.removeHistory(3);

        manager.getHistory();
        System.out.println("После удаления id 4 & 3^\n");
        manager.deleteEpicById(0);
        manager.getHistory();
        System.out.println("\nПосле удаления эпика\n");
        manager.getAllTasks();


    }
}
