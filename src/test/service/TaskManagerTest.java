package test.service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected TaskManager manager;
    protected Epic epic1, epic2, epic3;
    protected Subtask subtask1, subtask2, subtask3, subtask4;
    int lastEpicId;
    ArrayList<Task> emptyArray = new ArrayList<>();
    T getTaskManager() throws IOException, InterruptedException {
        return null;
    }

    @BeforeEach
    void taskManager() throws IOException, InterruptedException {
    this.manager = getTaskManager();
    this.epic1 = new Epic("Убраться в квартире","необходимо убрать лишний мусор", LocalDateTime.of(23,1,1,10,0),60);
    this.subtask1 = new Subtask("Пропылесосить пол","слишком пыльно!", StatusTask.DONE);
    this.epic2 = new Epic("Сходить в магазин","Надо купить продуктов");
    this.lastEpicId = epic1.getSuperId();

    this.subtask2 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");
    this.epic3 = new Epic("Сделать сальто", "Подготовиться");
    this.subtask3 = new Subtask("Научиться делать сальто","Надо посмотреть один видеоурок");
    }
    //  a. Со стандартным поведением.
    //  b. С пустым списком задач.
    //  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    //
    // c вариант я исключаю для get методов без параметров, думаю логично
    @Test
    void getAllTasksStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        Assertions.assertEquals(2,manager.getAllTasks().size());
    }

    @Test
    void getAllTasksEmpty() {
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteAllTasksStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.deleteAllTasks();
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteAllTasksEmpty() {
        manager.deleteAllTasks();
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void getByIdStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        int groupId = epic1.getEpicId();
        ArrayList<Task> groupArray = new ArrayList<>(List.of(epic1, subtask1));
        Assertions.assertEquals(groupArray, manager.getById(groupId));
    }

    @Test
    void getByIdEmpty() {
        Assertions.assertEquals(emptyArray, manager.getById(lastEpicId));
    }

    @Test
    void getByIdWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        Assertions.assertEquals(emptyArray, manager.getById(2));
    }

    @Test
    void createTaskStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        Assertions.assertEquals(epic1,manager.getEpic(lastEpicId));
        Assertions.assertEquals(subtask1,manager.getSubtask(lastEpicId + 1));
    }

    // Создавать таски на пустом менеджере нет необходимости, всегда ж так

    @Test
    void createTaskWrong() {
        int wrongId = -32;

        Assertions.assertNull(manager.getEpic(wrongId));
    }

    @Test
    void updateTaskStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);


        manager.updateTask(epic1);
        StatusTask statusEpic = manager.getEpic(lastEpicId).getStatus();
        Assertions.assertEquals(StatusTask.DONE,statusEpic);
    }

    @Test
    void updateTaskEmpty() {
        Assertions.assertNull(manager.updateTask(epic1));
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
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int lastSubtask = lastEpicId+1;
        manager.deleteSubtaskById(lastSubtask);
        Assertions.assertEquals(emptyArray, manager.getById(lastSubtask));
    }

    @Test
    void deleteSubtaskByIdEmpty() {
        manager.deleteSubtaskById(0);
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteSubtaskByIdWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteSubtaskById(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }

    @Test
    void deleteEpicByIdStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        manager.deleteEpicById(lastEpicId);
        Assertions.assertEquals(emptyArray, manager.getById(lastEpicId));
    }

    @Test
    void deleteEpicByIdEmpty() {
        manager.deleteEpicById(0);
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteEpicByIdWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteEpicById(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }

    @Test
    void deleteEpicOrSubtaskStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        manager.deleteEpicOrSubtask(lastEpicId);
        int lastSubtask = lastEpicId + 1;
        manager.deleteEpicOrSubtask(lastSubtask);

        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteEpicOrSubtaskEmpty() {
        manager.deleteEpicOrSubtask(lastEpicId);
        int lastSubtask = lastEpicId + 1;
        manager.deleteEpicOrSubtask(lastSubtask);

        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }


    @Test
    void deleteEpicOrSubtaskWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int wrongId = -23;
        ArrayList<Task> beforeDelete = manager.getAllTasks();
        manager.deleteEpicOrSubtask(wrongId);
        ArrayList<Task> afterDelete = manager.getAllTasks();

        Assertions.assertEquals(beforeDelete,afterDelete);
    }


    @Test
    void getAllSubtaskEpicStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        ArrayList<Task> testArray = new ArrayList<>();
        testArray.add(epic1);
        testArray.add(subtask1);

        Assertions.assertEquals(testArray,manager.getAllTasks());
    }


    @Test
    void getAllSubtaskEpicEmpty() {
        manager.deleteAllTasks();
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void getTaskStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int lastSubtask = lastEpicId + 1;
        Assertions.assertEquals(epic1, manager.getTask(lastEpicId));
        Assertions.assertEquals(subtask1, manager.getTask(lastSubtask));
    }

    @Test
    void getTaskEmpty() {
        Assertions.assertEquals(null, manager.getTask(0));
    }

    @Test
    void getTaskWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int wrongInt = -32;
        Assertions.assertNull(manager.getTask(wrongInt));
    }

    @Test
    void getSubtaskStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        int lastSubtaskId = lastEpicId + 1;
        Assertions.assertEquals(subtask1,manager.getSubtask(lastSubtaskId));
    }

    @Test
    void getSubtaskEmpty() {
        Assertions.assertNull(manager.getSubtask(0));
    }

    @Test
    void getSubtaskWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        int wrongInt = -32;
        Assertions.assertNull(manager.getSubtask(0));
    }

    @Test
    void getEpicStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);

        Assertions.assertEquals(epic1,manager.getEpic(lastEpicId));
    }

    @Test
    void getEpicEmpty() {
        Assertions.assertNull(manager.getEpic(0));
    }

    @Test
    void getEpicWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        int wrongInt = -32;
        Assertions.assertNull(manager.getEpic(wrongInt));
    }
    @Test
    void getHistoryStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        ArrayList<Task> history = new ArrayList<>();
        history.add(subtask1);
        history.add(epic1);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void getHistoryEmpty() {
        Assertions.assertNull(manager.getHistory());
    }

    @Test
    void removeHistoryStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        ArrayList<Task> history = new ArrayList<>();
        history.add(epic1);

        manager.removeHistory(lastSubtaskId);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void removeHistoryEmpty() {
        Assertions.assertDoesNotThrow( () -> manager.removeHistory(0) );
    }

    @Test
    void removeHistoryWrong() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.getEpic(lastEpicId);
        int lastSubtaskId = lastEpicId + 1;
        manager.getTask(lastSubtaskId);

        int wrongInt = -23;
        ArrayList<Task> history = new ArrayList<>();
        history.add(subtask1);
        history.add(epic1);

        manager.removeHistory(wrongInt);

        Assertions.assertEquals(history,manager.getHistory());
    }

    @Test
    void getPrioritizedTasksStandart() {
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(epic2);
        manager.getEpic(lastEpicId);
        int lastEpic2Id = lastEpicId + 2;
        manager.getTask(lastEpic2Id);

        ArrayList<Task> historyExpected = new ArrayList<>(
                List.of(epic1,epic2)
        );

        Assertions.assertEquals(historyExpected,manager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasksEmpty() {
        Assertions.assertNull(manager.getPrioritizedTasks());
    }

}