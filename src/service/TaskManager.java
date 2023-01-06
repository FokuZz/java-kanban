package service;

import org.jetbrains.annotations.NotNull;
import model.Epic;
import model.Subtask;
import model.Task;

public interface TaskManager {
    void getAllTasks();

    void deleteAllTasks();

    void getById(int id);

    void createTask(@NotNull Task task);

    Task updateTask(@NotNull Task task);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void deleteAllById(int id);

    void getAllSubtaskEpic(@NotNull Epic epic);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void getHistory();
}
