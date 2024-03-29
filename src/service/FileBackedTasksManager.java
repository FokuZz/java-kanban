package service;

import model.*;
import org.jetbrains.annotations.NotNull;
import service.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public FileBackedTasksManager() {

    }

    ;//Он мне нужен для создания класса без файла, без него будет только один конструктор

    protected FileBackedTasksManager(ArrayList<Epic> epics, ArrayList<Subtask> subtasks, int count, CustomLinkedList<Task> history) {
        setEpics(epics);
        setSubtasks(subtasks);
        setCounter(count);
        setCustomLinkedList(history);
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = Managers.getFileManager();
        Subtask epic = new Subtask("sd","sd");

        manager.createTask(epic);

    }

    static final Path fileName = Paths.get("FileTaskManager.CSV");

    protected void createFile() throws ManagerSaveException {
        if (!Files.exists(fileName)) {
            try {
                Files.createFile(fileName);
            } catch (IOException e) {
                throw new ManagerSaveException("Возникла ошибка при сохранении менеджера\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public void save() {
        char comma = ',';
        try {
            createFile();
        } catch (ManagerSaveException e) {
            e.getStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.toFile(), StandardCharsets.UTF_8))) {
            final String firstLine = "type,name,description,groupid,id,status,startTime,duration\n";
            bw.write(firstLine);
            if (super.getEpics().isEmpty() && super.getSubtasks().isEmpty()) {
                bw.write("all tasks empty\n");
            }
            for (Epic epic : super.getEpics()) {
                String writeFirst = epic.getClass().getName().substring(6) + comma + epic.getName() + comma
                        //////Нахожу тип класса//////////////////////////////////////Нахожу имя////////////
                        + epic.getDescription() + comma + epic.getEpicId() + comma
                        ////Нахожу описание////////////Нахожу групповой ид///////////
                        + epic.getSuperId() + comma + epic.getStatus() + comma + epic.getStartTime() + comma + epic.getDuration() + "\n";
                //Нахожу ид////////////////Нахожу статус///////////////////Дата старта//////////////////Длительность/////
                bw.write(writeFirst);
                for (Subtask subtask : super.getSubtasks()) {
                    if (epic.getEpicId() == subtask.getSubtaskId()) {
                        String writeSecond = subtask.getClass().getName().substring(6) + comma + subtask.getName()
                                //////Нахожу тип класса//////////////////////////////////////////Нахожу имя////
                                + comma + subtask.getDescription() + comma + subtask.getSubtaskId() + comma
                                /////////||///Нахожу описание/////|||||||///////Нахожу групповой ид////////|||
                                + subtask.getSuperId() + comma + subtask.getStatus() + comma + subtask.getStartTime() + comma + subtask.getDuration() + "\n";
                        //Нахожу ид////////////////Нахожу статус///////////////////Дата старта//////////////////Длительность/////
                        bw.write(writeSecond);
                    }


                }
            }

            bw.write("\n");


            if (super.getHistoryManager().size() == 0) {
                bw.write("history empty");
            } else {
                bw.write("history\n");
                int size = 0;
                for (Task task : super.getHistoryManager()) {
                    size++;

                    bw.write(Integer.toString(task.getSuperId()));
                    if (!(size == super.getHistoryManager().size())) {
                        bw.write(",");
                    }

                }
            }
            bw.write("\n\n");
            if (super.getHistoryManager().size() == 0) {
                bw.write("priority history empty");
            } else {
                bw.write("priority history\n");

                int size = 0;
                for (Task task : super.getPrioritizedTasks()) {
                    size++;

                    bw.write(Integer.toString(task.getSuperId()));
                    if (!(size == super.getPrioritizedTasks().size())) {
                        bw.write(",");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileBackedTasksManager loadFromFile(Path file) {

        ArrayList<Epic> epics = new ArrayList<>();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        CustomLinkedList<Task> history = new CustomLinkedList<>();
        int counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName.toFile()))) {
            if (!br.ready()) return new FileBackedTasksManager();
            br.readLine();
            String s = br.readLine();
            if (s.length() == 0) {
                System.out.println("В файле нет классов, перехожу к истории..");
                br.readLine();
            }
            while (s != null) {
                if (s.equals("")) {
                    s = br.readLine();
                    continue;
                }
                String[] array = s.split(",");

                if (array[0].equals("Epic") || array[0].equals("Subtask")) {
                    if (array[0].equals("Epic")) {
                        if (array[6].equals("null")) {
                            epics.add(new Epic(array[1], array[2]));
                        } else if (array[7].contains("H")) {
                            epics.add(new Epic(array[1], array[2], LocalDateTime.parse(array[6]), Integer.parseInt(array[7].replaceAll("PT", "").replaceAll("H", "")) * 60));
                        } else if (array[7].contains("M")){
                            epics.add(new Epic(array[1], array[2], LocalDateTime.parse(array[6]), Integer.parseInt(array[7].replaceAll("PT", "").replaceAll("M", ""))));
                        }
                        epics.get(epics.size() - 1).setEpicId(Integer.parseInt(array[3]));
                        counter++;
                        switch (array[5]) { // Быстрая проверка и конвертация из String в Enum
                            case ("NEW"):
                                epics.get(epics.size() - 1).setStatus(StatusTask.NEW);
                                break;
                            case ("DONE"):
                                epics.get(epics.size() - 1).setStatus(StatusTask.DONE);
                                break;
                            case ("IN_PROGRESS"):
                                epics.get(epics.size() - 1).setStatus(StatusTask.IN_PROGRESS);
                                break;
                        }
                    } else {
                        switch (array[5]) {
                            case ("NEW"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.NEW));
                                subtasks.get(subtasks.size() - 1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                subtasks.get(subtasks.size() - 1).setStartTime(LocalDateTime.parse(array[6]));
                                subtasks.get(subtasks.size() - 1).setDuration(Duration.parse(array[7]));
                                break;
                            case ("IN_PROGRESS"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.IN_PROGRESS));
                                subtasks.get(subtasks.size() - 1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                subtasks.get(subtasks.size() - 1).setStartTime(LocalDateTime.parse(array[6]));
                                subtasks.get(subtasks.size() - 1).setDuration(Duration.parse(array[7]));
                                break;
                            case ("DONE"):
                                subtasks.add(new Subtask(array[1], array[2], StatusTask.DONE));
                                subtasks.get(subtasks.size() - 1).setSubtaskId(epics.get(epics.size() - 1).getEpicId());
                                subtasks.get(subtasks.size() - 1).setStartTime(LocalDateTime.parse(array[6]));
                                subtasks.get(subtasks.size() - 1).setDuration(Duration.parse(array[7]));
                                break;
                        }
                    }
                } else if (s.equals("history")) {
                    s = br.readLine();
                    String[] arrayHistory = s.split(",");
                    for (int i = arrayHistory.length - 1; i >= 0; i--) { // и только тут проверяем историю по уникальным id
                        int j = Integer.parseInt(arrayHistory[i]);
                        for (Task task : epics) {
                            if (task.getSuperId() == j) {
                                history.linkLast(task);
                                break;
                            }
                        }
                        for (Task task : subtasks) {
                            if (task.getSuperId() == j) {
                                history.linkLast(task);
                                break;
                            }
                        }
                    }
                } else {
                    br.close();
                    break;
                }

                s = br.readLine();
            }

            return new FileBackedTasksManager(epics, subtasks, counter, history);
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
    public boolean createTask(@NotNull Task task) {
        boolean o = super.createTask(task);
        save();
        return o;
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
    public boolean deleteEpicOrSubtask(int superId) {
        boolean o = super.deleteEpicOrSubtask(superId);
        save();
        return o;
    }

    @Override
    public void removeHistory(int id) {
        super.removeHistory(id);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;

    }

    @Override
    public Task getTask(int id) {
        if (super.getTask(id) != null) {
            Task task = super.getTask(id);
            save();
            return task;
        }
        return null;
    }


}
