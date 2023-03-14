package test.service;

import model.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    static String fileName = "FileTaskManager.CSV";
    @Override
    FileBackedTasksManager getTaskManager(){
//        return new FileBackedTasksManager();
        return Managers.getFileManager();
    }

    @Test
    void saveEmptyListTaskAndEmptyHistory() {
        ((FileBackedTasksManager)manager).save();
        String file = null;
        try {
            file = readFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(file.contains("all tasks empty") && file.contains("history empty")
                && file.contains("priority history empty"));
    }

    @Test
    void saveOnlyOneEpic() {
        manager.createTask(epic1);
        ((FileBackedTasksManager)manager).save();
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