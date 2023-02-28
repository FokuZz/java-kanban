package test.service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager manager;
    protected Epic epic;
    protected Subtask subtask;

    T getTaskManager(){
        return null;
    }

    @BeforeEach
    void taskManager(){
    this.manager = getTaskManager();
    this.epic = new Epic("Убраться в квартире","необходимо убрать лишний мусор", LocalDateTime.of(23,1,1,10,0),60);
    this.subtask = new Subtask("Пропылесосить пол","слишком пыльно!", StatusTask.DONE);
    }

    @Test
    void getAllTasksStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        ArrayList<Task> allTasks = manager.getAllTasks();
        Assertions.assertEquals(2,allTasks.size());
    }

    @Test
    void getAllTasksEmptyTasks() {
        ArrayList<Task> allTasks = manager.getAllTasks();
        Assertions.assertEquals(0,allTasks.size());
    }

    @Test
    void getAllTasksWrongTasks() {
        manager.createTask(subtask);
        manager.createTask(subtask);
        ArrayList<Task> allTasks = manager.getAllTasks();
        Assertions.assertEquals(0,allTasks.size());
    }

    @Test
    void deleteAllTasks() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.deleteAllTasks();
        ArrayList<Task> allTasks = manager.getAllTasks();
        Assertions.assertTrue(allTasks.isEmpty());
    }

    @Test
    void getById() {
    }

    @Test
    void createTask() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void deleteSubtaskById() {
    }

    @Test
    void deleteEpicById() {
    }

    @Test
    void deleteEpicOrSubtask() {
    }

    @Test
    void getAllSubtaskEpic() {
    }

    @Test
    void getTask() {
    }

    @Test
    void getSubtask() {
    }

    @Test
    void getEpic() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void removeHistory() {
    }
}