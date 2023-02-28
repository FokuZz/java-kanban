package service;

import model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private ArrayList<Epic> epics = new ArrayList<>();
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int counter = 0;

    private boolean exception = false;

    public void setEpics(ArrayList<Epic> epics) {
        this.epics = epics;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
    public void setHistoryManager(ArrayList<Task> historyManager) {
        this.historyManager.setHistory(historyManager);
    }

    private void hrPrintln() {           //Быстрое создание линий на вывод
        System.out.println("---------------------------------");
    }

    @Override
    public ArrayList<Task> getAllTasks() {          // Выводит эпики и их сабтаски если у них один айди
        ArrayList<Task> allTasks = new ArrayList<>();

        if (epics.isEmpty()) {
            System.out.println("Нет задач\n");
            return null;
        }
        System.out.println("Начало списка вывода всех задач");
        for (Epic epic : epics) {
            hrPrintln();
            System.out.println(epic);
            allTasks.add(epic);
            for (Subtask subtask : subtasks) {
                if (epic.getEpicId() == subtask.getSubtaskId()) {
                    System.out.println(subtask);
                    allTasks.add(subtask);
                }
            }
        }
        hrPrintln();
        System.out.println("Конец списка вывода всех задач\n");
        return allTasks;
    }

    @Override
    public void deleteAllTasks() {          // Удаляет все задачи
        epics.clear();
        subtasks.clear();
        System.out.println("Все задачи были удаленны!\n");
    }

    @Override
    public ArrayList<Task> getById(int id) {         // Выводит эпики и их сабтаски по заданному айди
        ArrayList<Task> allTasks = new ArrayList<>();
        System.out.println("Начало списка поиска по ID");
        for (Epic epic : epics) {
            if (id == epic.getEpicId()) {
                hrPrintln();
                allTasks.add(epic);
                System.out.println(epic);
                for (Subtask subtask : subtasks) {
                    if (epic.getEpicId() == subtask.getSubtaskId()) {
                        System.out.println(subtask);
                        allTasks.add(subtask);
                    }
                }
                hrPrintln();
            }
        }
        System.out.println("Конец списка поиска по ID\n");
        return allTasks;
    }

    @Override
    public void createTask(@NotNull Task task){
        if(task.getClass().equals(Epic.class)){
            for(Epic epic:epics){
                if(task.isTaskCopy(epic)) return;
                if (((Epic) task).isTaskCopyTime(epic)){
                    exception = true;
                    return;
                }
            }
        } else {
            for(Subtask subtask: subtasks){
                if(task.isTaskCopy(subtask)) return;
            }
        }


                    //  Общий метод для 2х массивов Epics и Subtasks
        if (epics.contains(task) || subtasks.contains(task)) {
            System.out.println("Создания не произолшо, такая задача уже существует\n");
            return;
        } else if (task.getClass().equals(Epic.class)) {
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
    public Task updateTask(@NotNull Task task) {           // Обновляет статус задачи
        if (task.getClass().equals(Epic.class)) {
            int epicInt = -1;
            int countSub = 0; // Id эпика, от него будет создаваться новая группа сабтаск
            if(!subtasks.isEmpty()){
                for (Subtask subtask : subtasks) {
                    for(int i = 0; i < epics.size() ; i++){
                        if(task.isTaskCopy(epics.get(i))) {
                            epicInt = i;
                            if(task.isTaskCopy(epics.get(epicInt))){
                                break;
                            }
                        }
                    }
                    boolean sameId = ((Epic) task).getEpicId() == subtask.getSubtaskId();

                    if (sameId && subtask.getStatus().equals(StatusTask.NEW)) {//Минусует при обнаружении стандартного
                        subtask.setStatus(StatusTask.IN_PROGRESS); //статуса, если в подэпиках будет хоть один !Done тогда
                        epics.get(epicInt).addAmountSubtasks(); // число countSub не будет равно общему кол.ву
                        countSub++;                                        // сабтаск, из-за чего будет только In_progress
                    } else if (sameId && subtask.getStatus().equals(StatusTask.DONE)) {
                        epics.get(epicInt).addAmountSubtasks();
                        countSub--;
                    }
                }
            } else {
                for(int i = 0; i < epics.size() ; i++){
                    if(task.isTaskCopy(epics.get(i))) {
                        epicInt = i;
                        if(task.isTaskCopy(epics.get(epicInt))){
                            break;
                        }
                    }
                }
            }

            if (countSub != 0) epics.get(epicInt).setStatus(StatusTask.IN_PROGRESS);
            if (countSub < 0) {
                if (Math.abs(countSub) == epics.get(epicInt).getAmountSubtasks()) {
                    epics.get(epicInt).setStatus(StatusTask.DONE);
                }
            }

            return task = epics.get(epicInt);
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            if (task.getStatus().equals(StatusTask.NEW)) task.setStatus(StatusTask.IN_PROGRESS);
        }
        return task;
    }

    @Override
    public void deleteSubtaskById(int id) {
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getSubtaskId() != id) {
                subtaskTwin.add(subtask);
            }
        }
        subtasks = subtaskTwin;
    }

    @Override
    public void deleteEpicById(int epicId) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        for (Epic epic : epics) {
            if (epic.getEpicId() != epicId) {
                epicTwin.add(epic);

            } else if(epic.getAmountSubtasks()>0){
                ArrayList<Subtask> subtasksTwin = new ArrayList<>();
                historyManager.remove(epic);
                for (Subtask subtask: subtasks){
                    if (subtask.getSubtaskId() != epicId){
                        subtasksTwin.add(subtask);
                    }else historyManager.remove(subtask);
                }
                subtasks=subtasksTwin;
            }
        }
        epics = epicTwin;

    }

    @Override
    public void deleteEpicOrSubtask(int superId) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        for (Epic epic : epics) {
            if (epic.getSuperId() != superId) {
                epicTwin.add(epic);
            } else historyManager.remove(epic);
        }
        for (Subtask subtask : subtasks) {
            if (subtask.getSuperId() != superId) {
                subtaskTwin.add(subtask);
            } else historyManager.remove(subtask);
        }
        epics = epicTwin;
        subtasks = subtaskTwin;
    }

    @Override
    public ArrayList<Subtask> getAllSubtaskEpic(@NotNull Epic epic) {   //Выводит все сабтаски заданного епика
        ArrayList<Subtask> sub = new ArrayList<>();
        System.out.println("Начало подсписков задачи " + epic.getName());
        hrPrintln();
        for (Subtask subtask : subtasks) {
            if (epic.getEpicId() == subtask.getSubtaskId()) {
                System.out.println(subtask);
                sub.add(subtask);
            }
        }
        if(sub.isEmpty()){
            System.out.println("Ни одной подзадачи не найдено");
        }
        hrPrintln();
        System.out.println("Конец подсписков задачи " + epic.getName() + "\n");

        return sub;
    }

    @Override
    public Task getTask(int id) {
        Task task = null;
        for (Epic epic : epics) {
            if (epic.getSuperId() == id) {
                task = epic;
            }
        }
        for (Subtask subtask : subtasks) {
            if (subtask.getSuperId() == id) {
                task = subtask;
            }
        }
        if (task == null) {
            System.out.println("Данный ID = " + id + " не найден\nВозвращаю пустую задачу\n");
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Task task = getTask(id);
        if (!task.getClass().equals(Subtask.class)) {
            System.out.println("Данный ID = " + id + " не является Subtask\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Subtask) task;
    }

    @Override
    public Epic getEpic(int id) {
        Task task = getTask(id);
        if (!task.getClass().equals(Epic.class)) {
            System.out.println("Данный ID = " + id + " не является Epic\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Epic) task;
    }

    @Override
    public void getHistory() {
        ArrayList<Task> historyList = (ArrayList<Task>) historyManager.getHistory();
        System.out.println("Начало списка истории");
        hrPrintln();
        for (Task task : historyList) {
            System.out.println(task);
        }
        hrPrintln();
        System.out.println("Конец списка истории\n");
    }

    @Override
    public void removeHistory(int id) {
        for (Task epic : epics) {
            if (id == epic.getSuperId()) {
                historyManager.remove(epic);
                return;
            }
        }
        for (Task subtask : subtasks) {
            if (id == subtask.getSuperId()) {
                historyManager.remove(subtask);
                return;
            }
        }
    }

    public ArrayList<Epic> getEpics() {
        return epics;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public ArrayList<Task> getHistoryManager(){
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        return history;
    }

    protected int getCounter() {
        return this.counter;
    }
    
}
