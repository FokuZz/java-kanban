package manager.taskManager;

import org.jetbrains.annotations.NotNull;
import taskTracker.Epic;
import taskTracker.Subtask;
import taskTracker.Task;

import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Epic> epics = new ArrayList<>();
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private int counter = 0;

    private void hrPrintln(){           //Быстрое создание линий на вывод
        System.out.println("---------------------------------");
    }
    public void getAllTasks(){          // Выводит эпики и их сабтаски если у них один айди
        if (epics.isEmpty()){
            System.out.println("Нет задач\n");
            return;
        }
        System.out.println("Начало списка вывода всех задач");
        for (Epic epic : epics){
            hrPrintln();
            System.out.println(epic);
            for (Subtask subtask:subtasks){
                if (epic.getId()==subtask.getId()){
                    System.out.println(subtask);
                }
            }
        }
        hrPrintln();
        System.out.println("Конец списка вывода всех задач\n");
    }
    public void deleteAllTasks(){          // Удаляет все задачи
        epics.clear();
        subtasks.clear();
        System.out.println("Все задачи были удаленны!\n");
    }
    public void getById(int id){         // Выводит эпики и их сабтаски по заданному айди
        System.out.println("Начало списка поиска по ID");
        for (Epic epic : epics){
            if (id == epic.getId()){
                hrPrintln();
                System.out.println(epic);
                for (Subtask subtask:subtasks){
                    if (epic.getId()==subtask.getId()){
                        System.out.println(subtask);
                    }
                }
                hrPrintln();
            }
        }
        System.out.println("Конец списка поиска по ID\n");
    }
    public void createTask(@NotNull Task task){            //  Общий метод для 2х массивов Epics и Subtasks
        if(epics.contains(task)||subtasks.contains(task)){
            System.out.println("Создания не произолшо, такая задача уже существует\n");
            return;
        }else if (task.getClass().equals(Epic.class)) {
            task.setId(counter++);
            epics.add((Epic) task);
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            task.setId(epics.get(epics.size() - 1).getId());
            subtasks.add((Subtask) task);
        } else {
            System.out.println("Создания не произошло\n");
        }
    }
    public Task updateTask(@NotNull Task task){           // Обновляет статус задачи
        if (task.getClass().equals(Epic.class)) {
            int countSub=0;
            for (Subtask subtask:subtasks){
                boolean sameId = task.getId()==subtask.getId();
                if (sameId && subtask.getStatus().equals("NEW")){
                    subtask.setStatus("IN_PROGRESS");
                    epics.get(epics.indexOf(task)).addAmountSubtasks();
                    countSub++;
                } else if(sameId && subtask.getStatus().equals("DONE")){
                    epics.get(epics.indexOf(task)).addAmountSubtasks();
                    countSub--;
                }
            }
            if (countSub!=0) epics.get(epics.indexOf(task)).setStatus("IN_PROGRESS");
            if (countSub<0){
                if (Math.abs(countSub)==epics.get(epics.indexOf(task)).getAmountSubtasks()) {
                    epics.get(epics.indexOf(task)).setStatus("DONE");
                }
            }
            return task = epics.get(epics.indexOf(task));
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            if(task.getStatus().equals("NEW")) task.setStatus("IN_PROGRESS");
        }
        return task;
    }
    public void deleteSubtaskById(int id) {
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Subtask subtask: subtasks){
            if(subtask.getId()!=id){
                subtaskTwin.add(subtask);
            }
        }
        subtasks = subtaskTwin;
    }
    public void deleteEpicById(int id) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        for (Epic epic: epics){
            if (epic.getId()!=id){
                epicTwin.add(epic);
            }
        }
        epics = epicTwin;

    }
    public void deleteAllById(int id) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Epic epic: epics){
            if (epic.getId()!=id){
                epicTwin.add(epic);
            }
        }
        for (Subtask subtask: subtasks){
            if(subtask.getId()!=id){
                subtaskTwin.add(subtask);
            }
        }
        epics = epicTwin;
        subtasks = subtaskTwin;
    }
    public void getAllSubtaskEpic(@NotNull Epic epic){   //Выводит все сабтаски заданного епика
        System.out.println("Начало подсписков задачи "+epic.getName());
        hrPrintln();
        for (Subtask subtask: subtasks){
            if (epic.getId() == subtask.getId()){
                System.out.println(subtask);
            }
        }
        hrPrintln();
        System.out.println("Конец подсписков задачи "+epic.getName()+"\n");
    }
}
