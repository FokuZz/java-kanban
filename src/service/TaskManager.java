package service;

import org.jetbrains.annotations.NotNull;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    ArrayList<Task> getById(int id);

    boolean createTask(@NotNull Task task);

    Task updateTask(@NotNull Task task);

    void deleteSubtaskById(int id);

    void deleteEpicById(int epicId);

    boolean deleteEpicOrSubtask(int id);

    ArrayList<Subtask> getAllSubtaskEpic(@NotNull int groupId);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    ArrayList<Task> getHistory();

    void removeHistory(int superId);

    void clearHistory();
    ArrayList<Task> getPrioritizedTasks();
}
