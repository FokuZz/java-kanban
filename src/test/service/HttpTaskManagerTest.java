package test.service;

import com.google.gson.*;
import model.*;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import service.FileBackedTasksManager;
import service.HistoryManager;
import service.HttpTaskManager;
import service.TaskManager;
import service.exceptions.ManagerSaveException;
import com.google.gson.internal.reflect.ReflectionHelper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpTaskManagerTest {


    //WARNING: An illegal reflective access operation has occurred
    //WARNING: Illegal reflective access by com.google.gson.internal.reflect.ReflectionHelper (file://////java-kanban/lib/gson-2.9.0.jar) to field java.time.LocalDateTime.date
    //WARNING: Please consider reporting this to the maintainers of com.google.gson.internal.reflect.ReflectionHelper
    //WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
    //WARNING: All illegal access operations will be denied in a future release
    //
    // Надоела эта штука, пытался найти как её убрать, но ничего не поолучилось
    // да, я умею читать но я не знаю куда писать --illegal-ac...

    //Если не ошибаюсь это последнее тз по этому тренажёру, я конечно очень рад но это крутой опыт вот так прогрессировать
    // и потом уже понимать что ты мог сделать всё по другом совсем, в целом при необходимости я бы так и сделал
    // но я лишь хочу сделать тз, особенно сейчас сдавая его уже в просрок, чисто последние 2 дня я безвылазно и
    // максимальным сеансом в 20 часов сидел и делал это проклятое тз разбираясь с gson и
    // проклиная что нас не научили нормально его обрабатывать, до сих пор я толком нормально не разобрался но
    // всё же прогресс есть, спасибо вам что смотрите ревью, думаю это тоже очень
    // полезный навык сходу понимать что и где. Всех благ вам!
    static final int PORT = 8088;
     URI uri = URI.create("http://localhost:");
     Gson gson = new Gson();
     String api = "-50000";
    protected Epic epic1, epic2, epic3;
    protected Subtask subtask1, subtask2, subtask3;

    HttpResponse<String> response;

    KVServer kvServer;
    HttpTaskServer taskServer;
    static int lastId;

    @AfterEach
    void stop(){
        kvServer.stop();
        taskServer.stop();
    }
    @BeforeEach
    void startAndLoad() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer =new HttpTaskServer(8088);
            taskServer.start();
            {
                this.epic1 = new Epic("Убраться в квартире","необходимо убрать лишний мусор", LocalDateTime.of(23,1,1,10,0),60);
                this.subtask1 = new Subtask("Пропылесосить пол","слишком пыльно!", StatusTask.DONE);
                this.epic2 = new Epic("Сходить в магазин","Надо купить продуктов");
                this.subtask2 = new Subtask("Купить продукты для омлета", "Надо 180р. чтобы сделать омлет");
                this.epic3 = new Epic("Сделать сальто", "Подготовиться");
                this.subtask3 = new Subtask("Научиться делать сальто","Надо посмотреть один видеоурок");
            } // Создание эпиков
            HttpResponse<String> response = getResponse( "/register",8888);
            if(response.statusCode() == 200){
                api = response.body();
            }

            if (api.isEmpty()) {
                System.out.println("Ошибка, не удалось получить api");
                throw new IOException();
            }
        } catch (IOException | InterruptedException exp) {
            System.out.println("Ошибка сохранения");
            throw new RuntimeException(exp);
        }
    }



    @Test
    void getAllTasksStandart() {                                                                        //  /tasks/task
        try {
            response = postResponse("/tasks/task",8088,gson.toJson(epic1));
            if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                    +response.body());
            response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
            if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
            +response.body());

            response = getResponse("/tasks/task",PORT);
            if (response.statusCode() == 404 && response.statusCode() != 200 ) throw new RuntimeException("Пустой ответ"+"\n"
                    +response.body());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            ArrayList<Task> tasks =  new ArrayList<>(gson.fromJson(jsonElement.getAsJsonArray(), ArrayList.class));
            Assertions.assertEquals(2,tasks.size());



        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
       }

    }

    @Test
    void getAllTasksEmpty() throws IOException, InterruptedException{
            response = getResponse("/tasks/task",PORT);
            Assertions.assertEquals(404,response.statusCode());
    }


   @Test
    void deleteAllTasksStandart() throws IOException, InterruptedException{
           response = postResponse("/tasks/task",8088,gson.toJson(epic1));
           if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                   +response.body());
           response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
           if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                   +response.body());
           response = deleteResponse("/tasks/task",PORT);
           if (response.statusCode() != 200) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                   +response.body());

           response = getResponse("/tasks/task",PORT);
           Assertions.assertEquals(404,response.statusCode());
    }

   @Test
    void deleteAllTasksEmpty() throws IOException, InterruptedException{
            response = deleteResponse("/tasks/task",PORT);
            Assertions.assertEquals(200,response.statusCode());
    }


   @Test
    void getByIdStandart() throws IOException, InterruptedException {
       response = postResponse("/tasks/task",8088,gson.toJson(epic1));
       if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
               +response.body());
       response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
       if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
               +response.body());
       response = getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),8088);

       Assertions.assertTrue(epic1.isTaskCopy(gson.fromJson(response.body(),Epic.class)));
    }
    @Test
    void getByIdEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id="+6,8088);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void getByIdWrong() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id="+6,8088);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void createTaskStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        Assertions.assertEquals(201,response.statusCode());
    }
    @Test
    void createTaskWrong() throws IOException, InterruptedException{
        response = postResponse("/tasks/task",8088,"{YEEEES:sds}");
        Assertions.assertEquals(400,response.statusCode());
    }
    @Test
    void updateTaskStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());

        Assertions.assertEquals(StatusTask.DONE,gson.fromJson(
                getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),8088).body(),Epic.class).getStatus());//6id = Epic
        Assertions.assertEquals(StatusTask.DONE,gson.fromJson(
                getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),8088).body(),Subtask.class).getStatus());//7id = Subtask

    }

    @Test
    void deleteSubtaskByIdStandart() throws IOException, InterruptedException {

        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = deleteResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),PORT);
        if (response.statusCode() != 200) throw new RuntimeException("2 Удаления по id не произошло+, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = getResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void deleteSubtaskByIdEmpty() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void deleteSubtaskByIdWrong() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

   @Test
    void deleteEpicByIdStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = deleteResponse("/tasks/task?id=6",PORT);
        if (response.statusCode() != 200) throw new RuntimeException("2 Удаления по id не произошло+, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = getResponse("/tasks/task?id=6",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

  @Test
    void deleteEpicByIdEmpty() throws IOException, InterruptedException {
      response = deleteResponse("/tasks/task?id=7",PORT);
      Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void deleteEpicByIdWrong() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

  @Test
    void deleteEpicOrSubtaskStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = deleteResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),PORT);
        if (response.statusCode() != 200) throw new RuntimeException("2 Удаления по id не произошло+, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = deleteResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),PORT);
        if (response.statusCode() != 200) throw new RuntimeException("2 Удаления по id не произошло+, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();

        response = getResponse("/tasks/task",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void deleteEpicOrSubtaskEmpty() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void deleteEpicOrSubtaskWrong() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void getAllSubtaskEpicStandart() throws IOException, InterruptedException {//tasks/subtask/epic?id=
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask2));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(epic2));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask3));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();


        response = getResponse("/tasks/subtask/epic?id=0",PORT); //Нужен именно group ID
        if (response.statusCode() == 404 && response.statusCode() != 200 ) throw new RuntimeException("Пустой ответ"+"\n"
                +response.body());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        ArrayList<Task> tasks =  new ArrayList<>(gson.fromJson(jsonElement.getAsJsonArray(), ArrayList.class));
        Assertions.assertEquals(2,tasks.size());

    }
    @Test
    void getAllSubtaskEpicEmpty() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/subtask/epic?id=0",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void getTaskStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();



        response = getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),PORT);
        Assertions.assertEquals(200,response.statusCode());
    }

    @Test
    void getTaskEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void getTaskWrong() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void getSubtaskStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();



        response = getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),PORT);
        Assertions.assertEquals(200,response.statusCode());
    }

    @Test
    void getSubtaskEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }


    @Test
    void getSubtaskWrong() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=7",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void getEpicStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();



        response = getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),PORT);
        Assertions.assertEquals(200,response.statusCode());
    }
    @Test
    void getEpicEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=6",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }
    @Test
    void getEpicWrong() throws IOException, InterruptedException {
        response = getResponse("/tasks/task?id=6",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }


    @Test
    void getHistoryStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();
        response = postResponse("/tasks/task",8088,gson.toJson(epic2));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask2));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),PORT);
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),PORT);
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+2),PORT);
        response = getResponse("/tasks/history",PORT);
        if (response.statusCode() != 200) throw new RuntimeException("История не была найдена, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getHistory();
        JsonElement jsonElement = JsonParser.parseString(response.body());
        ArrayList<Task> tasks =  new ArrayList<>(gson.fromJson(jsonElement.getAsJsonArray(), ArrayList.class));
        Assertions.assertEquals(3,tasks.size());

    }

    @Test
    void getHistoryEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/history",PORT);
        Assertions.assertEquals(404,response.statusCode());
    }

    @Test
    void removeHistoryStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = deleteResponse("/tasks/task",8088);
        Assertions.assertEquals(200,response.statusCode());
    }
    @Test
    void removeHistoryEmpty() throws IOException, InterruptedException {
        response = deleteResponse("/tasks/task",8088);
        Assertions.assertEquals(200,response.statusCode());
    }

    @Test
    void getPrioritizedTasksStandart() throws IOException, InterruptedException {
        response = postResponse("/tasks/task",8088,gson.toJson(epic1));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask1));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getAllTasks();
        response = postResponse("/tasks/task",8088,gson.toJson(epic2));
        if (response.statusCode() != 201) throw new RuntimeException("Epic1 не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        response = postResponse("/tasks/task",8088,gson.toJson(subtask2));
        if (response.statusCode() != 201) throw new RuntimeException("Subtask не был добавлен, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getHistory();
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()),PORT);
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+1),PORT);
        getResponse("/tasks/task?id="+Integer.valueOf(taskServer.getFirstEpicId()+2),PORT);
        response = getResponse("/tasks/",PORT);
        if (response.statusCode() != 200) throw new RuntimeException("История не была найдена, ошибка   StatusCode = "+response.statusCode()+"\n"
                +response.body());
        taskServer.getManager().getHistory();
        JsonElement jsonElement = JsonParser.parseString(response.body());
        ArrayList<Task> tasks =  new ArrayList<>(gson.fromJson(jsonElement.getAsJsonArray(), ArrayList.class));
        Assertions.assertEquals(3,tasks.size());
    }

    @Test
    void getPrioritizedTasksEmpty() throws IOException, InterruptedException {
        response = getResponse("/tasks/",8088);
        Assertions.assertEquals(404,response.statusCode());
    }

    private HttpResponse<String> getResponse(String url, int port) throws IOException, InterruptedException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(uri + String.valueOf(port) + url))
                    .build();

        } catch (Exception e) {
            System.out.println("Что-то не так при создании запроса у метода GET\nURI = " + URI.create(uri + String.valueOf(port) + url));
        }
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteResponse(String url, int port) throws IOException, InterruptedException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .DELETE()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(uri + String.valueOf(port) + url))
                    .build();

        } catch (Exception e) {
            System.out.println("Что-то не так при создании запроса у метода DELETE\nURI = " + URI.create(uri + String.valueOf(port) + url));
        }
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postResponse(String url, int port, String json) throws IOException, InterruptedException {
        HttpRequest request = null;
        try {
            if (json.isEmpty()) throw new IOException("Пустое тело запроса");
            request = HttpRequest.newBuilder()
                    .uri(URI.create(uri + String.valueOf(port) + url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Content-Type", "application/json")
                    .build();

        } catch (Exception e) {
            System.out.println("Что-то не так при создании запроса у метода POST\nURI = " + URI.create(uri + String.valueOf(port) + url) + " "
                    + "Body = " + json);
        }
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}