package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FileBackedTasksManager extends InMemoryTaskManager{
    Path fileName = Paths.get("FileTaskManager.CSV");
    private void save(){
        char comma = ',';
        boolean firstTry = false;
        if(!Files.exists(fileName)){
            try {
                Files.createFile(fileName);
            } catch (IOException e) {
                e.getStackTrace();
            }
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.toFile(), StandardCharsets.UTF_8))){
                final String firstLine = "type,name,description,groupid,id,status\n";
                bw.write(firstLine);
        for(Epic epic : super.getEpics()){
            String write = epic.getClass().getName() + comma + epic.getName() + comma + epic.getDescription() + comma + epic.getEpicId() + comma + epic.getSuperId() + comma + epic.getStatus()+"\n";
                          //////Нахожу тип класса/////////////////Нахожу имя////////////////Нахожу описание////////////Нахожу групповой ид/////////////Нахожу ид////////////////Нахожу статус///
            bw.write(write);
        }
        for(Subtask subtask : super.getSubtasks()){
                String write = subtask.getClass().getName() + comma + subtask.getName() + comma + subtask.getDescription() + comma + subtask.getSubtaskId() + comma + subtask.getSuperId() + comma + subtask.getStatus()+"\n";
                               //////Нахожу тип класса/////////////////Нахожу имя/////////////||///Нахожу описание/////|||||||///////Нахожу групповой ид////////|||||||/////Нахожу ид////////////|////Нахожу статус///||
                bw.write(write);
        }
        bw.write("\n");
        int size=0;

        if(super.getHistoryManager().size()==0) bw.write("history empty");

        for(Task task : super.getHistoryManager()){
            size++;

            bw.write(Integer.toString(task.getSuperId()));
            if(!(size == super.getHistoryManager().size())){
                bw.write(",");
            }

        }
        }catch (IOException exp){
            exp.getStackTrace();
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
    @Override
    public void createTask(@NotNull Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public Task updateTask(@NotNull Task task) {
        Task taskReturn = super.updateTask(task);
        save();
        return taskReturn;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteEpicOrSubtask(int superId) {
        super.deleteEpicOrSubtask(superId);
        save();
    }

    @Override
    public void removeHistory(int id) {
       super.removeHistory(id);
       save();
    }
}
