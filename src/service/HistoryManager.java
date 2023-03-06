package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(Task task);

    List<Task> getHistory();

    void setHistory(ArrayList<Task> historyManager);

    ArrayList<Task> getPrioritizedHistory();
}