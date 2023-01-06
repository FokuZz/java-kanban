package model;

import service.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> listHistory = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (listHistory.size() == 10 ) listHistory.remove(0);
        listHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return listHistory;

    }
}
