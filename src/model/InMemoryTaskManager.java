package model;

import service.HistoryManager;
import service.TaskManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private ArrayList<Epic> epics = new ArrayList<>();
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int counter = 0;

    private void hrPrintln(){           //Быстрое создание линий на вывод
        System.out.println("---------------------------------");
    }
    @Override
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
                if (epic.getEpicId()==subtask.getSubtaskId()){
                    System.out.println(subtask);
                }
            }
        }
        hrPrintln();
        System.out.println("Конец списка вывода всех задач\n");
    }

    @Override
    public void deleteAllTasks(){          // Удаляет все задачи
        epics.clear();
        subtasks.clear();
        System.out.println("Все задачи были удаленны!\n");
    }

    @Override
    public void getById(int id){         // Выводит эпики и их сабтаски по заданному айди

        System.out.println("Начало списка поиска по ID");
        for (Epic epic : epics){
            if (id == epic.getEpicId()){
                hrPrintln();
                System.out.println(epic);
                for (Subtask subtask:subtasks){
                    if (epic.getEpicId()==subtask.getSubtaskId()){
                        System.out.println(subtask);
                    }
                }
                hrPrintln();
            }
        }
        System.out.println("Конец списка поиска по ID\n");
    }

    @Override
    public void createTask(@NotNull Task task){            //  Общий метод для 2х массивов Epics и Subtasks
        if(epics.contains(task) || subtasks.contains(task)){
            System.out.println("Создания не произолшо, такая задача уже существует\n");
            return;
        }else if (task.getClass().equals(Epic.class)) {
            ((Epic) task).setEpicId(counter++);
            epics.add((Epic) task);
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            ((Subtask) task).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
            subtasks.add((Subtask) task);
        } else {
            System.out.println("Создания не произошло\n");
        }
    }

    @Override
    public Task updateTask(@NotNull Task task){           // Обновляет статус задачи
        if (task.getClass().equals(Epic.class)) {
            int countSub=0;
            for (Subtask subtask : subtasks){
                boolean sameId = ((Epic) task).getEpicId() == subtask.getSubtaskId();

                if (sameId && subtask.getStatus().equals(StatusTask.NEW)){
                    subtask.setStatus(StatusTask.IN_PROGRESS);
                    epics.get(epics.indexOf(task)).addAmountSubtasks();
                    countSub++;
                } else if(sameId && subtask.getStatus().equals(StatusTask.DONE)){
                    epics.get(epics.indexOf(task)).addAmountSubtasks();
                    countSub--;
                }
            }
            if (countSub!=0) epics.get(epics.indexOf(task)).setStatus(StatusTask.IN_PROGRESS);
            if (countSub<0){
                if (Math.abs(countSub)==epics.get(epics.indexOf(task)).getAmountSubtasks()) {
                    epics.get(epics.indexOf(task)).setStatus(StatusTask.DONE);
                }
            }
            return task = epics.get(epics.indexOf(task));
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            if(task.getStatus().equals(StatusTask.NEW)) task.setStatus(StatusTask.IN_PROGRESS);
        }
        return task;
    }

    @Override
    public void deleteSubtaskById(int id) {
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Subtask subtask: subtasks){
            if(subtask.getSubtaskId()!=id){
                subtaskTwin.add(subtask);
            }
        }
        subtasks = subtaskTwin;
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        for (Epic epic: epics){
            if (epic.getEpicId()!=id){
                epicTwin.add(epic);
            }
        }
        epics = epicTwin;

    }

    @Override
    public void deleteAllById(int id) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Epic epic: epics){
            if (epic.getEpicId()!=id){
                epicTwin.add(epic);
            }
        }
        for (Subtask subtask: subtasks){
            if(subtask.getSubtaskId()!=id){
                subtaskTwin.add(subtask);
            }
        }
        epics = epicTwin;
        subtasks = subtaskTwin;
    }

    @Override
    public void getAllSubtaskEpic(@NotNull Epic epic){   //Выводит все сабтаски заданного епика
        System.out.println("Начало подсписков задачи "+epic.getName());
        hrPrintln();
        for (Subtask subtask: subtasks){
            if (epic.getEpicId() == subtask.getSubtaskId()){
                System.out.println(subtask);
            }
        }
        hrPrintln();
        System.out.println("Конец подсписков задачи "+epic.getName()+"\n");
    }

    @Override
    public Task getTask(int id) {
        Task task = null;
        for (Epic epic: epics) {
            if (epic.getSuperId() == id) {
                task = epic;
            }
        }
        for (Subtask subtask: subtasks){
            if (subtask.getSuperId() == id){
                task = subtask;
            }
        }
        if (task == null){
            System.out.println("Данный ID = "+id+" не найден\nВозвращаю пустую задачу\n");
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Task task = getTask(id);
        if (!task.getClass().equals(Subtask.class)){
            System.out.println("Данный ID = "+id+" не является Subtask\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Subtask) task;
    }

    @Override
    public Epic getEpic(int id) {
        Task task = getTask(id);
        if (!task.getClass().equals(Epic.class)){
            System.out.println("Данный ID = "+id+" не является Epic\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Epic) task;
    }

    @Override
    public void getHistory(){
        ArrayList<Task> historyList = (ArrayList<Task>) historyManager.getHistory();
        System.out.println("Начало списка истории");
        hrPrintln();
        for (Task task : historyList){
            System.out.println(task);
        }
        hrPrintln();
        System.out.println("Конец списка истории\n");
    }

}
