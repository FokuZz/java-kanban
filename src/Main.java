import model.*;
import server.HttpTaskServer;
import server.KVServer;
import service.HttpTaskManager;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new HttpTaskServer(8080).start();
//        new KVServer().start();
//        HttpTaskManager taskManager = Managers.getDefault();
////        taskManager.deleteAllTasks();
//        Epic epic1 = new Epic("Сделать зарядку", "пройтись по списку");
//        Subtask subtask1 = new Subtask("5 ажуманий", "без астнаовки",StatusTask.NEW);
//        Subtask subtask2 = new Subtask("10 приседаний", "без астнаовки",StatusTask.NEW);
//        Subtask subtask3 = new Subtask("15 кувырков", "без астнаовки",StatusTask.NEW);
//
//
//        taskManager.createTask(epic1);
//        taskManager.createTask(subtask1);
//        taskManager.createTask(subtask2);
//        taskManager.createTask(subtask3);

//        Epic epic1 = new Epic("32323", "syy2323232er");
//        Subtask subtask1 = new Subtask("N22323232aat2t", "32323rr");
//
//        taskManager.createTask(epic1);
//        taskManager.createTask(subtask1);





    }

}
