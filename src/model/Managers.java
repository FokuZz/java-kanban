package model;

import service.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    static Path fileName = Paths.get("FileTaskManager.CSV");
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {return FileBackedTasksManager.loadFromFile(fileName);}
}
