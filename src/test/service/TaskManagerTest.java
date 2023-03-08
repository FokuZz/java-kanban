package test.service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected TaskManager manager;
    protected Epic epic,epic2;
    protected Subtask subtask;
    int lastEpicId;
    ArrayList<Task> emptyArray = new ArrayList<>();
    T getTaskManager(){
        return null;
    }

    @BeforeEach
    void taskManager(){
    this.manager = getTaskManager();
    this.epic = new Epic("Убраться в квартире","необходимо убрать лишний мусор", LocalDateTime.of(23,1,1,10,0),60);
    this.subtask = new Subtask("Пропылесосить пол","слишком пыльно!", StatusTask.DONE);
    this.epic2 = new Epic("Сходить в магазин","Надо купить продуктов");
    this.lastEpicId = epic.getSuperId();
    }
    //  a. Со стандартным поведением.
    //  b. С пустым списком задач.
    //  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    //
    // c вариант я исключаю для get методов без параметров, думаю логично
    @Test
    void getAllTasksStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        Assertions.assertEquals(2,manager.getAllTasks().size());
    }

    @Test
    void getAllTasksEmpty() {
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void deleteAllTasksStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.deleteAllTasks();
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void deleteAllTasksEmpty() {
        manager.deleteAllTasks();
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void getByIdStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        int groupId = epic.getEpicId();
        ArrayList<Task> groupArray = new ArrayList<>(List.of(epic,subtask));
        Assertions.assertEquals(groupArray, manager.getById(groupId));
    }

    @Test
    void getByIdEmpty() {
        Assertions.assertEquals(emptyArray, manager.getById(lastEpicId));
    }

    @Test
    void getByIdWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);
        Assertions.assertEquals(emptyArray, manager.getById(2));
    }

    @Test
    void createTaskStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        Assertions.assertEquals(epic,manager.getEpic(lastEpicId));
        Assertions.assertEquals(subtask,manager.getSubtask(lastEpicId + 1));
    }

    // Создавать таски на пустом менеджере нет необходимости, всегда ж так

    @Test
    void createTaskWrong() {
        int wrongId = -32;

        Assertions.assertThrows(
                NullPointerException.class
                ,() -> manager.getEpic(wrongId));
    }

    @Test
    void updateTaskStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        manager.updateTask(epic);
        StatusTask statusEpic = manager.getEpic(lastEpicId).getStatus();
        Assertions.assertEquals(StatusTask.DONE,statusEpic);
    }

    @Test
    void updateTaskEmpty() {
        Assertions.assertNull(manager.updateTask(epic));
    }

    @Test
    void updateTaskWrong() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> manager.updateTask(null)
        );
    }

    @Test
    void deleteSubtaskByIdStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int lastSubtask = lastEpicId+1;
        manager.deleteSubtaskById(lastSubtask);
        Assertions.assertEquals(emptyArray, manager.getById(lastSubtask));
    }

    @Test
    void deleteSubtaskByIdEmpty() {
        manager.deleteSubtaskById(0);
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void deleteSubtaskByIdWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteSubtaskById(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }

    @Test
    void deleteEpicByIdStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        manager.deleteEpicById(lastEpicId);
        Assertions.assertEquals(emptyArray, manager.getById(lastEpicId));
    }

    @Test
    void deleteEpicByIdEmpty() {
        manager.deleteEpicById(0);
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void deleteEpicByIdWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteEpicById(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }

    @Test
    void deleteEpicOrSubtaskStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        manager.deleteEpicOrSubtask(lastEpicId);
        int lastSubtask = lastEpicId + 1;
        manager.deleteEpicOrSubtask(lastSubtask);

        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void deleteEpicOrSubtaskEmpty() {
        manager.deleteEpicOrSubtask(lastEpicId);
        int lastSubtask = lastEpicId + 1;
        manager.deleteEpicOrSubtask(lastSubtask);

        Assertions.assertNull(manager.getAllTasks());
    }


    @Test
    void deleteEpicOrSubtaskWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteEpicOrSubtask(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }


    @Test
    void getAllSubtaskEpicStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        ArrayList<Task> testArray = new ArrayList<>();
        testArray.add(epic);
        testArray.add(subtask);

        Assertions.assertEquals(testArray,manager.getAllTasks());
    }


    @Test
    void getAllSubtaskEpicEmpty() {
        Assertions.assertNull(manager.getAllTasks());
    }

    @Test
    void getTaskStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int lastSubtask = lastEpicId + 1;
        Assertions.assertEquals(epic, manager.getTask(lastEpicId));
        Assertions.assertEquals(subtask, manager.getTask(lastSubtask));
    }

    @Test
    void getTaskEmpty() {
        Assertions.assertEquals(null, manager.getTask(0));
    }

    @Test
    void getTaskWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int wrongInt = -32;
        Assertions.assertNull(manager.getTask(wrongInt));
    }

    @Test
    void getSubtaskStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        int lastSubtaskId = lastEpicId + 1;
        Assertions.assertEquals(subtask,manager.getSubtask(lastSubtaskId));
    }

    @Test
    void getSubtaskEmpty() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> manager.getSubtask(0));
    }

    @Test
    void getSubtaskWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);
        int wrongInt = -32;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> manager.getSubtask(wrongInt));
    }

    @Test
    void getEpicStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);

        Assertions.assertEquals(epic,manager.getEpic(lastEpicId));
    }

    @Test
    void getEpicEmpty() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> manager.getEpic(0));
    }

    @Test
    void getEpicWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);
        int wrongInt = -32;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> manager.getEpic(wrongInt));
    }
    @Test
    void getHistoryStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        ArrayList<Task> history = new ArrayList<>();
        history.add(subtask);
        history.add(epic);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void getHistoryEmpty() {
        Assertions.assertNull(manager.getHistory());
    }

    @Test
    void removeHistoryStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        ArrayList<Task> history = new ArrayList<>();
        history.add(epic);

        manager.removeHistory(lastSubtaskId);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void removeHistoryEmpty() {
        Assertions.assertDoesNotThrow( () -> manager.removeHistory(0) );
    }

    @Test
    void removeHistoryWrong() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        int wrongInt = -23;
        ArrayList<Task> history = new ArrayList<>();
        history.add(subtask);
        history.add(epic);

        manager.removeHistory(wrongInt);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void getPrioritizedTasksStandart() {
        manager.createTask(epic);
        manager.createTask(subtask);
        manager.createTask(epic2);
        manager.getEpic(lastEpicId);
        int lastEpic2Id = lastEpicId + 2;
        manager.getTask(lastEpic2Id);

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic,epic2)
        );

        Assertions.assertEquals(historyExpected,manager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasksEmpty() {
        Assertions.assertNull(manager.getPrioritizedTasks());
    }

}