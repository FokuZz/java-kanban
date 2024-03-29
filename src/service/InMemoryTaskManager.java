package service;

import model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private ArrayList<Epic> epics = new ArrayList<>();
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private int counter = 0;

    private HashMap<LocalDateTime, Boolean> oneYearTimeTask = new HashMap<>();


    public void setEpics(ArrayList<Epic> epics) {
        this.epics = epics;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void setCustomLinkedList(CustomLinkedList<Task> historyManager) {
        this.historyManager.setHistory(historyManager.getTasks());
    }

    private void hrPrintln() {           //Быстрое создание линий на вывод
        System.out.println("---------------------------------");
    }

    @Override
    public ArrayList<Task> getAllTasks() {          // Выводит эпики и их сабтаски если у них один айди
        ArrayList<Task> allTasks = new ArrayList<>();

        if (epics.isEmpty()) {
            System.out.println("Нет задач\n");
            return new ArrayList<>();
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
        oneYearTimeTask = new HashMap<>();
        epics.clear();
        subtasks.clear();
        counter = 0;
        System.out.println("Все задачи были удаленны!\n");
    }

    @Override
    public ArrayList<Task> getById(int id) {         // Выводит эпики и их сабтаски по заданному айди
        ArrayList<Task> allTasks = new ArrayList<>();
        System.out.println("Начало списка поиска по ID");
        hrPrintln();

        for (Epic epic : epics) {
            if (id == epic.getEpicId()) {
                allTasks.add(epic);
                System.out.println(epic);
            }
            for (Subtask subtask : subtasks) {
                if (id == subtask.getSubtaskId()) {
                    allTasks.add(subtask);
                }
            }

        }
        hrPrintln();
        System.out.println("Конец списка поиска по ID\n");
        return allTasks;
    }

    @Override
    public boolean createTask(@NotNull Task task) {
        int max = 0;
        if (!epics.isEmpty() || !subtasks.isEmpty()) {
            for (Epic epic : epics) {
                if (epic.getSuperId() > max) {
                    max = epic.getSuperId();
                }
            }
            for (Subtask subtask : subtasks) {
                if (subtask.getSuperId() > max) {
                    max = subtask.getSuperId();
                }
            }
        }
        counter = max;
        if (oneYearTimeTask.isEmpty()) {
            for (LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0)
                 ; time.isBefore(LocalDateTime.of(2024, 1, 1, 0, 0))
                    ; time = time.plusMinutes(15)) {
                oneYearTimeTask.put(time, false);
            }
        }

        if (task.getClass().equals(Epic.class)) {
            for (Epic epic : epics) {
                if (task.isTaskCopy(epic)) {
                    System.out.println("Создания не произолшо, такая задача уже существует\n");
                    return false;
                }
            }
        } else {
            for (Subtask subtask : subtasks) {
                if (task.isTaskCopy(subtask)){
                    System.out.println("Создания не произолшо, такая задача уже существует\n");
                    return false;
                }
            }
        }

        //  Общий метод для 2х массивов Epics и Subtasks
        if (epics.contains(task) || subtasks.contains(task)) {
            System.out.println("Создания не произолшо, такая задача уже существует\n");
            return false;
        } else if (task.getClass().equals(Epic.class)) {
            ((Epic) task).setEpicId(counter++);

            if (task.getStartTime() != null) {
                LocalDateTime time = task.getStartTime();
                if (oneYearTimeTask.containsKey(time)) {
                    if (oneYearTimeTask.get(time)) {
                        System.out.println("Время выполнение задачи, пересекается с другим епиком\nСоздания не произошло \n");
                        return false;
                    } else {
                        while (time.isBefore(((Epic) task).getEndTime())) {
                            oneYearTimeTask.put(time, true);
                            time = time.plusMinutes(15);
                        }


                    }
                }
            }
            epics.add((Epic) task);
            return true;
        } else if (task.getClass().equals(Subtask.class) && !epics.isEmpty()) {
            Subtask sub = ((Subtask) task);
            sub.setSubtaskId(epics.get(epics.size() - 1).getEpicId());
            sub.setStartTime(epics.get(epics.size() - 1).getStartTime());
            sub.setDuration(epics.get(epics.size() - 1).getDuration());
            subtasks.add((Subtask) task);
            for (Epic epic : epics){
                if(((Subtask) task).getSubtaskId() == epic.getEpicId()){
                    updateTask(epic);
                }
            }
            return true;
        } else if (task.getClass().equals(Subtask.class)){
            System.out.println("Создание сабтаска без эпика невозможно\n");
            return false;
        }
        System.out.println("Создания не произошло\n");
        return false;
    }

    @Override
    public Task updateTask(@NotNull Task task) {           // Обновляет статус задачи
        if (task.getClass().equals(Epic.class)) {
            int epicInt = -1;
            int countSub = 0; // Id эпика, от него будет создаваться новая группа сабтаск
            if (!subtasks.isEmpty()) {
                for (int i = 0; i < epics.size(); i++) {
                    if (task.isTaskCopy(epics.get(i))) {
                        epicInt = i;
                        if (task.isTaskCopy(epics.get(epicInt))) {
                            break;
                        }
                    }
                }
                if (epicInt == -1) {
                    System.out.println("Такого эпика не существует\nАпдейта не произошло\n");
                    return null;
                }
                if(epics.get(epicInt).getStatus().equals(StatusTask.DONE)) return task;
                for (Subtask subtask : subtasks) {

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
            } else if (!epics.isEmpty()) {
                for (int i = 0; i < epics.size(); i++) {
                    if (task.isTaskCopy(epics.get(i))) {
                        epicInt = i;
                        if (task.isTaskCopy(epics.get(epicInt))) {
                            break;
                        }
                    }
                }
            } else {
                return null;
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

            }
        }
        epics = epicTwin;

    }

    @Override
    public boolean deleteEpicOrSubtask(int superId) {
        ArrayList<Epic> epicTwin = new ArrayList<>();
        ArrayList<Subtask> subtaskTwin = new ArrayList<>();
        boolean isRemoveHasBeen = false;
        for (Epic epic : epics) {
            if (epic.getSuperId() != superId) {
                epicTwin.add(epic);
            } else {
                isRemoveHasBeen = true;
                historyManager.remove(epic);
                LocalDateTime time = epic.getStartTime();
                while (time.isBefore(epic.getEndTime())) {
                    oneYearTimeTask.put(time, false);
                    time = time.plusMinutes(15);
                }
            }
        }
        for (Subtask subtask : subtasks) {
            if (subtask.getSuperId() != superId) {
                subtaskTwin.add(subtask);
            } else {
                isRemoveHasBeen = true;
                historyManager.remove(subtask);
            }
        }
        epics = epicTwin;
        subtasks = subtaskTwin;
        return isRemoveHasBeen;
    }

    @Override
    public ArrayList<Subtask> getAllSubtaskEpic(@NotNull int groupId) {   //Выводит все сабтаски заданного епика
        ArrayList<Subtask> sub = new ArrayList<>();
        Epic epic = null;
        for (Epic o : epics) {
            if (groupId == o.getEpicId()) {
                epic = o;
                break;
            }
        }
        if (epic == null) {
            System.out.println("По данному GroupId = " + groupId + ", ничего не найдено\nВозвращаю пустое значение");
            return null;
        }
        System.out.println("Начало подсписков задачи " + epic.getName());
        hrPrintln();
        for (Subtask subtask : subtasks) {
            if (epic.getEpicId() == subtask.getSubtaskId()) {
                System.out.println(subtask);
                sub.add(subtask);
            }
        }
        if (sub.isEmpty()) {
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
                break;
            }
        }
        for (Subtask subtask : subtasks) {
            if (subtask.getSuperId() == id) {
                task = subtask;
                break;
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
        if (task == null) return null;
        if (!task.getClass().equals(Subtask.class)) {
            System.out.println("Данный ID = " + id + " не является Subtask\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Subtask) task;
    }

    @Override
    public Epic getEpic(int id) {
        Task task = getTask(id);
        if (task == null) return null;
        if (!task.getClass().equals(Epic.class)) {
            System.out.println("Данный ID = " + id + " не является Epic\nВозвращаю пустую задачу\n");
            return null;
        }

        return (Epic) task;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        ArrayList<Task> historyList = (ArrayList<Task>) historyManager.getHistory();
        if (historyList.isEmpty()) {
            System.out.println("История пустая, возвращаю пустое число");
            return null;
        }
        System.out.println("Начало списка истории");
        hrPrintln();
        for (Task task : historyList) {
            System.out.println(task);
            history.add(task);
        }
        hrPrintln();
        System.out.println("Конец списка истории\n");
        return history;
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

    public ArrayList<Task> getHistoryManager() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        return history;
    }

    public int getCounter() {
        return this.counter;
    }

    public ArrayList<Task> getPrioritizedTasks() {
        ArrayList<Task> historyList = historyManager.getPrioritizedHistory();
        if (historyList.isEmpty()) {
            System.out.println("История пустая, возвращаю пустое число");
            return null;
        }
        System.out.println("Начало списка приоритетной истории по времени");
        hrPrintln();
        for (Task task : historyList) {
            System.out.println(task);
        }
        hrPrintln();
        System.out.println("Конец списка приоритетной истории по времени\n");
        return historyList;
    }

    @Override
    public void clearHistory() {
        oneYearTimeTask = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }
}
