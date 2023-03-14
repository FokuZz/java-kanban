import model.*;
import server.HttpTaskServer;
import server.KVServer;
import service.HttpTaskManager;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer(8080);
            new KVServer().start();
            taskServer.start();
            Epic epic1 = new Epic("Сделать зарядку", "пройтись по списку");
            Subtask subtask1 = new Subtask("5 ажуманий", "без астнаовки", StatusTask.NEW);
            Subtask subtask2 = new Subtask("10 приседаний", "без астнаовки", StatusTask.NEW);
            Subtask subtask3 = new Subtask("15 кувырков", "без астнаовки", StatusTask.NEW);

            taskServer.getManager().createTask(epic1);
            taskServer.getManager().createTask(subtask1);
            taskServer.getManager().createTask(subtask2);
            taskServer.getManager().createTask(subtask3);

            taskServer.getManager().getAllTasks();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Пардон что оставил тут такой безобразный код, в целом ничего особенного. Я так и не понял как взаимодействие
        //сделать между внешним менеджером и внутренним сделать, через кучу сохранений я не хотел, так как и так ужас в
        // консоли твориться, как по мне использование одного менеджера внутри обработки эндпоинтов самый лучший
        // хотя скорее всего это было изначально так сделать задуманно. Спасибо за ревью!



    }

}
