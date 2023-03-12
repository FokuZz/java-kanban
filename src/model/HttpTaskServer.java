package model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpServer;
import service.FileBackedTasksManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskServer {
    static Gson gson = new Gson();
    private static int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    static FileBackedTasksManager fileManager;

    public static void main(String[] args) throws IOException {
        fileManager = Managers.getFileManager();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        System.out.println("Http сервак стартанул PORT = "+PORT);
    }

    static class TaskHandler implements HttpHandler {
        HashMap<String,Integer> hashLocalDT = new HashMap<>();
        HashMap<String,String> hashBody = new HashMap<>();


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int idTask = -1;

            String secondPath = getSecondPostPath(exchange).orElse("");
            String[] arrPath = exchange.getRequestURI().getPath().split("/");
            System.out.println("handle запустился, иду к методам");
            if(exchange.getRequestURI().getRawQuery() != null) {
                idTask = Integer.parseInt(
                        List.of(
                                exchange.getRequestURI().getRawQuery().split("id=")
                        ).get(1)
                ); //Делю всё что выходит за ? на id=, запихиваю в лист и сразу забираю число которое написал
            }  // Обработка параметра строки запроса
            // Либо я дурак, либо ничего не сказали как обрабатывать параметр строки от, клиент запроса
            // Если располагаете информацией, можете-пожалуйста написать как лучше было бы, и проще это сделать
            // (всё это говорю, так-как ощущение, что сделал всё на костылях, а не нормальным путём)

            if (exchange.getRequestMethod().equals("GET") ){

                if(secondPath.equals("task") && arrPath.length == 3){
                    if(idTask != -1){
                        writeResponse(exchange,gson.toJson(fileManager.getTask(idTask)),200);
                        return;
                    }
                    writeResponse(exchange,gson.toJson(fileManager.getAllTasks()),200);
                    return;
                } // GET /tasks/task DONE and /task/task?id= DONE

                if(secondPath.equals("epic") && arrPath.length == 3){
                    if(idTask != -1){
                        if(fileManager.getEpic(idTask) == null) {
                            writeResponse(exchange
                                    ,"По этому ID = "+String.valueOf(idTask)+", Epic не найден"
                                    ,404);
                        }
                        writeResponse(exchange,gson.toJson(fileManager.getEpic(idTask)),200);
                        return;
                    }
                    writeResponse(exchange,gson.toJson(fileManager.getEpics()),200);
                    return;
                } // GET /tasks/epic DONE and /task/epic?id= DONE

                if(secondPath.equals("subtask")
                        && arrPath.length == 3){
                    if(idTask != -1){
                        if(fileManager.getSubtask(idTask) == null) {
                            writeResponse(exchange
                                    ,"По этому ID = "+String.valueOf(idTask)+", Subtask не найден"
                                    ,404);
                        }
                        writeResponse(exchange,gson.toJson(fileManager.getSubtask(idTask)),200);
                        return;
                    }
                    writeResponse(exchange,gson.toJson(fileManager.getSubtasks()),200);
                    return;
                }                // GET /tasks/subtask DONE and /task/subtask?id= DONE

                if(secondPath.equals("history") && arrPath.length == 3
                        && fileManager.getHistory() != null){
                    writeResponse(exchange,gson.toJson(fileManager.getHistory()),200);
                    return;
                }                        // GET /tasks/history DONE

                if(arrPath.length == 2 && arrPath[arrPath.length-1].equals("tasks")
                        && fileManager.getPrioritizedTasks() != null){
                    writeResponse(exchange,gson.toJson(fileManager.getPrioritizedTasks()),200);
                    return;
                }               // GET /tasks/    DONE

                if(secondPath.equals("subtask") && arrPath[arrPath.length-1].equals("epic")
                        && arrPath.length == 4){
                    if(idTask != -1) {
                        if (fileManager.getAllSubtaskEpic(idTask) == null) {
                            writeResponse(exchange
                                    , "По этому GroupId = " + String.valueOf(idTask) + ",  ничего не найдено"
                                    , 404);
                        }
                    }

                    writeResponse(exchange,gson.toJson(fileManager.getAllSubtaskEpic(idTask)),200);
                    return;
                }                                                            // GET /tasks/subtask/epic?id= DONE

                if(secondPath.equals("post") && arrPath[arrPath.length-1].equals("help")
                        && arrPath.length == 4){
                    String epicText = "Как правильно заполнять POST запрос для Epic Task\n";
                    String epicHelp1 = "1 конструктор\n{\n" +
                            "\t\"type\": \"Epic\",\n" +
                            "\t\"startTime\": {\n" +
                            "\t\t\"date\": {\n" +
                            "\t\t\t\"year\": 2023,\n" +
                            "\t\t\t\"month\": 1,\n" +
                            "\t\t\t\"day\": 2\n" +
                            "\t\t},\n" +
                            "\t\t\"time\": {\n" +
                            "\t\t\t\"hour\": 12,\n" +
                            "\t\t\t\"minute\": 45,\n" +
                            "\t\t\t\"second\": 0,\n" +
                            "\t\t\t\"nano\": 0\n" +
                            "\t\t}\n" +
                            "\t},\n" +
                            "\t\"duration\": {\n" +
                            "\t\t\"seconds\": 1800,\n" +
                            "\t\t\"nanos\": 0\n" +
                            "\t},\n" +
                            "\t\"name\": \"Сходить в магазин\",\n" +
                            "\t\"description\": \"Надо купить продуктов\"\n" +
                            "}\n\n\n";
                    String epicHelp2 = "2 конструктор\n{\n" +
                            "\t\"type\": \"Epic\",\n" +
                            "\t\"name\": \"Сходить в магазин\",\n" +
                            "\t\"description\": \"Надо купить продуктов\"\n" +
                            "}\n\n\n\n";

                    String subtaskText = "Как правильно заполнять POST запрос для Subtask Task\n";
                    String subtaskHelp1 = "1 конструктор\n{\n" +
                            "\t\"type\":\"Subtask\",\n" +
                            "\t\"name\": \"Купить молоко\",\n" +
                            "\t\"description\": \"Стоимость 80 рублей\",\n" +
                            "\t\"status\": \"DONE\"\n" +
                            "}\n\n\n";
                    String subtaskHelp2 = "2 конструктор\n{\n" +
                            "\t\"type\":\"Subtask\",\n" +
                            "\t\"name\": \"Купить молоко\",\n" +
                            "\t\"description\": \"Стоимость 80 рублей\"\n" +
                            "}";

                    writeResponse(exchange,epicText+epicHelp1+epicHelp2
                            +subtaskText+subtaskHelp1+subtaskHelp2,200);
                    return;
                }                           // GET /tasks/post/help DONE
            }
            if (exchange.getRequestMethod().equals("POST")){
                String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                if(body.isEmpty()) {
                    writeResponse(exchange,"Пустое тело запроса post",400);
                    return;
                }
                body = body.replaceAll("\"", "");
                if(secondPath.equals("task") && arrPath.length == 3){
                    System.out.println("Зашёл в пост");
                    if(body.contains("startTime")){
                        for(String text: body.split(",")){
                            try {
                                if (text.contains("startTime")) putInHashLDT("year", text);
                                if (text.contains("month")) putInHashLDT("month", text);
                                if (text.contains("day")) putInHashLDT("day", text);

                                if (text.contains("hour")) putInHashLDT("hour", text);
                                if (text.contains("minute")) putInHashLDT("minute", text);
                                if (text.contains("second") && !text.contains("seconds")) putInHashLDT("second", text);
                                if (text.contains("nano") && !text.contains("nanos")) putInHashLDT("nano", text);

                                if (text.contains("duration")) putInHashLDT("seconds", text);
                                if (text.contains("nanos")) putInHashLDT("nanos", text);

                                if (text.contains("type")) putInHashString("type", text);
                                if (text.contains("name")) putInHashString("name", text);
                                if (text.contains("description")) putInHashString("description", text);
                            } catch (Exception e){
                                writeResponse(exchange,"Json был заполнен неккоректно",400);
                                return;
                            }
                        }
                    } else {
                        body = body.replaceAll("\"", "");
                        String[] first = body.split(",");
                        for(String text: first){
                            String[] forHashmap = text.trim().replaceAll("\\}","")
                                    .replaceAll("\\{","").trim().split(":");
                            System.out.println(Arrays.toString(forHashmap));
                            hashBody.put(forHashmap[0],forHashmap[1]);
                        } // Честно я думаю это лучшее решение, чтобы создавать новые таски, во-первых, по типу, во-вторых, по
                        // логике конструктора
                    }
                    System.out.println("pass1");
                    Task task = null;
                    try {
                        if (hashBody.get("type").equals("Epic")) {
                            if(hashLocalDT.isEmpty()){
                                task = new Epic(
                                        hashBody.get("name"),
                                        hashBody.get("description")
                                );
                            }
                            else {
                                task = new Epic(
                                        hashBody.get("name")
                                        ,hashBody.get("description")
                                        ,LocalDateTime.of(hashLocalDT.get("year")
                                                ,hashLocalDT.get("month"),hashLocalDT.get("day")
                                                ,hashLocalDT.get("hour"), hashLocalDT.get("minute")
                                                ,hashLocalDT.get("second"), hashLocalDT.get("nano"))
                                        ,Duration.ofSeconds(hashLocalDT.get("seconds")
                                                ,hashLocalDT.get("nanos")).toMinutesPart()
                                );
                            }
                            boolean istrue = fileManager.createTask(task);
                            if (!istrue){
                                writeResponse(exchange,"Создания не произошло, скорее всего такой" +
                                        "Epic уже есть",400);
                                return;
                            }
                            fileManager.updateTask(task); //c психу, потому-что одна не обновляла
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            hashBody.clear();
                            hashLocalDT.clear();
                            writeResponse(exchange,"Epic был успешно добавлен",201);
                            return;
                        }
                        else if (hashBody.get("type").equals("Subtask")) {
                            if(hashBody.containsKey("status")){
                                StatusTask status = null;
                                switch (hashBody.get("status")){
                                    case "NEW":
                                        status = StatusTask.NEW;
                                    break;
                                    case "DONE":
                                        status = StatusTask.DONE;
                                        break;
                                    case "IN_PROGRESS":
                                        status = StatusTask.IN_PROGRESS;
                                        break;
                                    default:
                                        writeResponse(exchange,"Такого статуса не существует",404);
                                        return;
                                }
                                task = new Subtask(
                                        hashBody.get("name"),
                                        hashBody.get("description"),
                                        status
                                );
                            } else {
                                task = new Subtask(
                                        hashBody.get("name"),
                                        hashBody.get("description")
                                );
                            }
                            boolean istrue = fileManager.createTask(task);
                            if (!istrue){
                                writeResponse(exchange,"Создания не произошло, скорее всего такой" +
                                        "Subtask уже есть",400);
                                return;
                            }
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            hashBody.clear();
                            hashLocalDT.clear();
                            writeResponse(exchange,"Subtask был успешно добавлен",201);
                            return;
                        }
                        if(task == null){
                            writeResponse(exchange,"Json был заполнен неккоректно",400);
                            return;
                        }

                    } catch (JsonSyntaxException exp){
                        writeResponse(exchange,"Некорректно введён json",400);
                        return;
                    }

                }  // POST /tasks/task/body {task ..}  DONE

            }
            if (exchange.getRequestMethod().equals("DELETE")){

                if(secondPath.equals("task") && arrPath.length == 3 && idTask != -1){
                        if(!fileManager.deleteEpicOrSubtask(idTask)) {
                            writeResponse(exchange
                                    ,"Удаления по этому ID = "+String.valueOf(idTask)+", не произошло"
                                    ,404);
                        }
                        fileManager.removeHistory(idTask);
                        writeResponse(exchange,"Удаление произошло успешно",200);
                        return;
                } // DELETE /tasks/task/?id= DONE

                if(secondPath.equals("task") && arrPath.length == 3){
                    fileManager.deleteAllTasks();
                    fileManager.removeAllHistory();
                } //  DELETE /tasks/task/ DONE

            }
            // на delete /tasks/(subtask или epic) просто сил нет уже
            // и как по мне бесполезно, если я делаю реализацию для кнопок, то зачем мне парится?
            // нужная кнопка в нужном месте ведь будет
            writeResponse(exchange,"Nothing was find",404);
        }
        private void putInHashLDT(String value,String text){
            String newText = text.substring(text.indexOf(value));
            String[] split = newText.replaceAll("\\s", "")

                    .replaceAll("\\}","").split(":");
            hashLocalDT.put(split[0],Integer.parseInt(split[1]));
        }

        private void putInHashString(String value,String text){
            String newText = text.substring(text.indexOf(value));
            String[] split = newText.replaceAll("\\}","").split(":");
            hashBody.put(split[0],split[1].trim());
        }

        private Optional<String> getSecondPostPath(HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(pathParts[2]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                return Optional.empty();
            }
        }


        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            OutputStream os = exchange.getResponseBody();
            if(responseString == null){
                exchange.sendResponseHeaders(400,0);
                os.write("Переданный текст равен null".getBytes());
            }
            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                    os.write(bytes);

            }
            os.close();
            exchange.close();
        }
    }
}
