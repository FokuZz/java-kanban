package model;

import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.util.ArrayList;

public class HttpLoadHelper {

    ArrayList<Epic> epics = new ArrayList<>();
    ArrayList<Subtask> subtasks = new ArrayList<>();
    int count = 0;
    ArrayList<Integer> history = new ArrayList<>();

    public HttpLoadHelper() {
    }

    public HttpLoadHelper(ArrayList<Epic> epics, ArrayList<Subtask> subtasks, int count, ArrayList<Integer> history) {
        setEpics(epics);
        setSubtasks(subtasks);
        setCounter(count);
        setHistory(history);
    }

    public HttpLoadHelper(ArrayList<Epic> epics, ArrayList<Subtask> subtasks, int count) {
        setEpics(epics);
        setSubtasks(subtasks);
        setCounter(count);
    }


    public ArrayList<Epic> getEpics() {
        return epics;
    }

    public void setEpics(ArrayList<Epic> epics) {
        this.epics = epics;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public int getCount() {
        return count;
    }

    public void setCounter(int count) {
        this.count = count;
    }

    public ArrayList<Integer> getHistory() {
        return history;
    }

    public HistoryManager getHistoryManager() {
        if(history == null){
            return new InMemoryHistoryManager();
        }
        ArrayList<Epic> epics = getEpics();
        ArrayList<Subtask> subtasks = getSubtasks();
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Epic epic : epics) {

            allTasks.add(epic);
            for (Subtask subtask : subtasks) {
                if (epic.getEpicId() == subtask.getSubtaskId()) {
                    allTasks.add(subtask);
                }
            }
        }
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (Task task : allTasks) {
            for (Integer id : getHistory()) {
                if (task.getSuperId() == id) {
                    historyManager.add(task);
                }
            }
        }
        return historyManager;
    }

    public void setAllTasks(ArrayList<Task> tasks){
        for (Task task: tasks){
            if(task.getClass().equals(Epic.class)){
                addEpic((Epic) task);
            } else {
                addSubtask((Subtask) task);
            }
        }
    }

    public void addEpic(Epic epic){
        epics.add(epic);
    }

    public void addSubtask(Subtask subtask){
        subtasks.add(subtask);
    }

    public void setHistory(ArrayList<Integer> history) {
        this.history = history;
    }
}
