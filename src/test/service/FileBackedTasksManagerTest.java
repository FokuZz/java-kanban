package test.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.InMemoryTaskManager;
import service.exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    static String fileName = "FileTaskManager.CSV";
    @Override
    FileBackedTasksManager getTaskManager(){
        return new FileBackedTasksManager();
    }

    @Test
    void saveEmptyListTaskAndEmptyHistory() {
        ((FileBackedTasksManager)manager).saveToFile();
        String file = null;
        try {
            file = readFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(file.contains("all tasks empty") && file.contains("history empty")
                && file.contains("priority history empty"));

        // Сложно делать такие тесты, так как не понимаю как сравнивать правильно файлы
        // можно написать логику для вывода в string, но пишу и так в крайне быстром темпе, для меня
        // то что он выполняет без ошибки это уже победа, думаю тут просто не хватает опыта в работе с файлами
        // я посмотрел в интернете и не нашёл быстрого варианта аля File.of чтобы можно было сразу текстом написать для
        // быстрого сравнения.
        // Был бы рад услышать возможные варианты, уверен, что есть какой-то простой вариант

        //Между прошлым мной и настоящим прошло 30 минут и всё-таки пришло что-то в голову (до этого Assertions не было)
    }

    @Test
    void saveOnlyOneEpic() {
        manager.createTask(epic);
        ((FileBackedTasksManager)manager).saveToFile();
        String file = null;
        try {
            file = readFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(file.contains("Epic,Убраться в квартире,необходимо убрать лишний мусор,0,"+lastEpicId+",NEW,0023-01-01T10:00,PT1H"));

    }


    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}