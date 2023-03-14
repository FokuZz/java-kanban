package model;

import service.*;
import service.exceptions.ManagerSaveException;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    static Path fileName = Paths.get("FileTaskManager.CSV");
    public static HttpTaskManager getDefault(String portKVSERVER) {
        try {
            return HttpTaskManager.loadFromFile(URI.create("http://localhost:"+portKVSERVER));
        } catch (ManagerSaveException e) {
            System.out.println("Проблема загрузки менеджера с сервера");
            throw new RuntimeException(e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {return FileBackedTasksManager.loadFromFile(fileName);}

    public static InMemoryTaskManager getInMemoryManager() {return new InMemoryTaskManager();}
}
