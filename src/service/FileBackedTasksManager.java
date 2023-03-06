package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import model.*;
import org.jetbrains.annotations.NotNull;
import service.exceptions.ManagerSaveException;

public class FileBackedTasksManager extends InMemoryTaskManager{
    public FileBackedTasksManager(){};//Он мне нужен для создания класса без файла, без него будет только один конструктор
    private FileBackedTasksManager(ArrayList<Epic> epics, ArrayList<Subtask> subtasks, int count, ArrayList<Task> history, TreeSet<Task> treeSet){
        setEpics(epics);
        setSubtasks(subtasks);
        setCounter(count);
        setHistoryManager(history);
        setPriorityHistory(treeSet);
    }
    public static void main(String[] args){
//        FileBackedTasksManager manager = new FileBackedTasksManager();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(fileName);
        Epic epic1 = new Epic("Сходить в магазин","Надо купить продуктов", LocalDateTime.of(2023,1,1,0,0),30);
        Subtask subtask1 = new Subtask("Купить молоко", "Стоимость 80 рублей",StatusTask.DONE);
        Subtask subtask2 = new Subtask("Купить яица","Стоимость 100 рублей", StatusTask.DONE);
        Epic epic2 = new Epic("Сделать омлет", "Надо приготовить омлет из продуктов", LocalDateTime.of(2023,1,1,0,45),30);
        Subtask subtask3 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");


        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        manager.createTask(epic2);
        manager.createTask(subtask3);
        manager.getAllTasks();
        System.out.println("Это до обновления статуса^\n");
        manager.updateTask(epic1);
        manager.updateTask(epic2);
        manager.getAllTasks();
        System.out.println("Это после обновления статуса^\n");
        manager.getTask(0);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getTask(3);
        manager.getTask(4);
        manager.getHistory();
        System.out.println("Это история с 4 разными вызовами^\n");
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getEpic(0);
        manager.getTask(4);
        manager.getTask(3);
        manager.getHistory();
        System.out.println("После множества повторных вызовов Get^\n");
        manager.removeHistory(4);
        manager.removeHistory(3);
        manager.deleteEpicOrSubtask(5);
        manager.deleteEpicOrSubtask(10);

        manager.getHistory();
        manager.getAllTasks();
    }
    static Path fileName = Paths.get("FileTaskManager.CSV");

    private void createFile() throws ManagerSaveException {
        if(!Files.exists(fileName)){
            try {
                Files.createFile(fileName);
            } catch (IOException e) {
                throw new ManagerSaveException("Возникла ошибка при сохранении менеджера\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }
    private void save(){
        char comma = ',';
        try {
            createFile();
        } catch (ManagerSaveException e) {
            e.getStackTrace();
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.toFile(), StandardCharsets.UTF_8))){
                final String firstLine = "type,name,description,groupid,id,status,startTime,duration\n";
                bw.write(firstLine);
        for(Epic epic : super.getEpics()){
            String writeFirst = epic.getClass().getName().substring(6) + comma + epic.getName() + comma
                        //////Нахожу тип класса//////////////////////////////////////Нахожу имя////////////
                    + epic.getDescription() + comma + epic.getEpicId() + comma
                    ////Нахожу описание////////////Нахожу групповой ид///////////
                    + epic.getSuperId() + comma + epic.getStatus()+ comma + epic.getStartTime()+ comma + epic.getDuration() +"\n";
                    //Нахожу ид////////////////Нахожу статус///////////////////Дата старта//////////////////Длительность/////
            bw.write(writeFirst);
            for(Subtask subtask : super.getSubtasks()){
                if(epic.getEpicId() == subtask.getSubtaskId()) {
                    String writeSecond = subtask.getClass().getName().substring(6) + comma + subtask.getName()
                                      //////Нахожу тип класса//////////////////////////////////////////Нахожу имя////
                            + comma + subtask.getDescription() + comma + subtask.getSubtaskId() + comma
                            /////////||///Нахожу описание/////|||||||///////Нахожу групповой ид////////|||
                            + subtask.getSuperId() + comma + subtask.getStatus()+ comma + subtask.getStartTime()+ comma + subtask.getDuration() +"\n";
                            //Нахожу ид////////////////Нахожу статус///////////////////Дата старта//////////////////Длительность/////
                    bw.write(writeSecond);
                }


            }
        }

        bw.write("\n");


        if(super.getHistoryManager().size()==0){
            bw.write("history empty\n");
        } else {
            bw.write("history\n");
        }
            int size=0;
        for(Task task : super.getHistoryManager()){
            size++;

            bw.write(Integer.toString(task.getSuperId()));
            if(!(size == super.getHistoryManager().size())){
                bw.write(",");
            }

        }
            bw.write("\n\n");
            if(super.getPrioritizedTasks().size()==0){
                bw.write("priority history empty");
            } else {
                bw.write("priority history\n");
            }
            size=0;
            for(Task task : super.getPrioritizedTasks()){
                size++;

                bw.write(Integer.toString(task.getSuperId()));
                if(!(size == super.getPrioritizedTasks().size())){
                    bw.write(",");
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileBackedTasksManager loadFromFile(Path file){

        ArrayList<Epic> epics = new ArrayList<>();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayList<Task> history = new ArrayList<>();
        TreeSet<Task> priorityHistory = new TreeSet<>(Task::compareTime);
        int counter = 0;
        try(BufferedReader br = new BufferedReader (new FileReader(fileName.toFile()))) {
            if(!br.ready()) return new FileBackedTasksManager();

            br.readLine();
            String s = br.readLine();
            if(s.length() == 0){
                System.out.println("В файле нет классов, перехожу к истории..");
                br.readLine();
            }
            while ( s != null) {
                if(s.equals("")) {
                    s = br.readLine();
                    continue;
                }
                String[] array = s.split(",");

                if (array[0].length() > 2 && !s.equals("history empty") && !s.equals("priority history empty")) {
                    if (array[0].equals("Epic")) {
                        if(array[6] == null){
                            epics.add(new Epic(array[1], array[2]));
                        } else {
                            epics.add(new Epic(array[1], array[2],LocalDateTime.parse(array[6]),Integer.parseInt(array[7].substring(2,3))));
                        }
                        epics.get(epics.size()-1).setEpicId(Integer.parseInt(array[3]));
                        counter++;
                        switch (array[5]) { // Быстрая проверка и конвертация из String в Enum
                            case ("NEW"):
                                epics.get(epics.size()-1).setStatus(StatusTask.NEW);
                                break;
                            case ("DONE"):
                                epics.get(epics.size()-1).setStatus(StatusTask.DONE);
                                break;
                            case ("IN_PROGRESS"):
                                epics.get(epics.size()-1).setStatus(StatusTask.IN_PROGRESS);
                                break;
                        }
                    } else {
                        switch (array[5]) {
                            case ("NEW"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.NEW));
                                subtasks.get(subtasks.size()-1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                break;
                            case ("IN_PROGRESS"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.IN_PROGRESS));
                                subtasks.get(subtasks.size()-1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                break;
                            case ("DONE"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.DONE));
                                subtasks.get(subtasks.size()-1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                break;
                        }
                    }
                } else {
                    br.close();
                    break;
                }
                if(!s.equals("history empty")) {
                    String[] arrayHistory = s.split(",");
                    for (int i = arrayHistory.length - 1; i >= 0; i--) { // и только тут проверяем историю по уникальным id
                        int j = Integer.parseInt(arrayHistory[i]);
                        for (Task task : epics) {
                            if (task.getSuperId() == j) {
                                history.add(task);
                                break;
                            }
                        }
                        for (Task task : subtasks) {
                            if (task.getSuperId() == j) {
                                history.add(task);
                                break;
                            }
                        }
                    }
                }
                s = br.readLine();
            }

            return new FileBackedTasksManager(epics,subtasks,counter,history,priorityHistory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
    @Override
    public void createTask(@NotNull Task task){
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
