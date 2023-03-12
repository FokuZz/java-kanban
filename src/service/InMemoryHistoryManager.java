package service;

import model.CustomLinkedList;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> history = new CustomLinkedList<>();
    private ArrayList<Task> prioritizedHistory = new ArrayList<>();


    @Override
    public void add(Task task) {
        history.linkLast(task);
        prioritizedHistory = history.getTasks(); // Беру уникальную коллекцию, чтобы отсортировать
        Collections.sort(prioritizedHistory,Task::compareTime);
    }

    @Override
    public void remove(Task task){
        history.remove(task);
        prioritizedHistory.remove(task);
    }

    public void setHistory(ArrayList<Task> history) {
        CustomLinkedList<Task> testHistory = new CustomLinkedList<>();
        for(int i = history.size() - 1; i >= 0  ; i--){
            testHistory.linkLast(history.get(i));
        }
        this.history = testHistory;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public ArrayList<Task> getPrioritizedHistory(){
        prioritizedHistory = history.getTasks();
        Collections.sort(prioritizedHistory,Task::compareTime);
        return prioritizedHistory;
    }
}
