package test.model;

import model.Epic;
import model.Managers;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1;
    TaskManager manager;

    @BeforeEach
    public void createManagerAndEpic(){
        manager = Managers.getDefault();
        epic1 = new Epic("Имя тест епика1","Описание тест епика1");
        manager.createTask(epic1);
    }

    @Test
    public void emptyListSubtasks() {

        manager.updateTask(epic1);
        Assertions.assertEquals(0,manager.getAllSubtaskEpic(epic1.getEpicId()).size());
    }

    @Test
    public void allSubtasksIsNEW() {

        Subtask subtask1 = new Subtask("Имя тест сабтаск1", "Описание тест сабтаск1", StatusTask.NEW);
        Subtask subtask2 = new Subtask("Имя тест сабтаск2","Описание тест сабтаск2", StatusTask.NEW);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        ArrayList<Subtask> sub = manager.getAllSubtaskEpic(epic1.getEpicId());
        Assertions.assertEquals(2,sub.size());
        for(Subtask subtask : sub){
            Assertions.assertEquals(StatusTask.NEW,subtask.getStatus());
        }
        manager.updateTask(epic1);
        sub = manager.getAllSubtaskEpic(epic1.getEpicId());
        for(Subtask subtask : sub){
            Assertions.assertEquals(StatusTask.IN_PROGRESS,subtask.getStatus());
        }
    }

    @Test
    public void allSubtasksIsDONE() {

        Subtask subtask1 = new Subtask("Имя тест сабтаск1", "Описание тест сабтаск1", StatusTask.DONE);
        Subtask subtask2 = new Subtask("Имя тест сабтаск2","Описание тест сабтаск2", StatusTask.DONE);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        ArrayList<Subtask> sub = manager.getAllSubtaskEpic(epic1.getEpicId());
        Assertions.assertEquals(2,sub.size());
        for(Subtask subtask : sub){
            Assertions.assertEquals(StatusTask.DONE,subtask.getStatus());
        }
    }

    @Test
    public void allSubtasksIsNEWAndDONE() {

        Subtask subtask1 = new Subtask("Имя тест сабтаск1", "Описание тест сабтаск1", StatusTask.NEW);
        Subtask subtask2 = new Subtask("Имя тест сабтаск2","Описание тест сабтаск2", StatusTask.DONE);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        ArrayList<Subtask> sub = manager.getAllSubtaskEpic(epic1.getEpicId());
        Assertions.assertEquals(2,sub.size());
        boolean isHasNew = false;
        boolean isHasDone = false;
        for(Subtask subtask : sub){
            if(subtask.getStatus() == StatusTask.NEW){
               isHasNew = true;
           } else if(subtask.getStatus() == StatusTask.DONE){
                isHasDone = true;
            }
        }
        Assertions.assertTrue(isHasNew);
        Assertions.assertTrue(isHasDone);
        }


    @Test
    public void allSubtasksIsIN_PROGRESS() {

        Subtask subtask1 = new Subtask("Имя тест сабтаск1", "Описание тест сабтаск1", StatusTask.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Имя тест сабтаск2","Описание тест сабтаск2", StatusTask.IN_PROGRESS);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        ArrayList<Subtask> sub = manager.getAllSubtaskEpic(epic1.getEpicId());
        Assertions.assertEquals(2,sub.size());
        for(Subtask subtask : sub){
            Assertions.assertEquals(StatusTask.IN_PROGRESS,subtask.getStatus());
        }

    }

}