package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.StatusTask;
import model.Subtask;
import model.Task;
import service.HttpTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    static Gson gson = new Gson();
    private int port;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    static HttpTaskManager fileManager;
    HttpServer server;
    int lastMemory;


    public HttpTaskServer(int port) throws IOException {
        this.port = port;
        fileManager = new HttpTaskManager("http://localhost:8888");
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TaskHandler());
    }
    public int getFirstEpicId(){
        if(!fileManager.getEpics().isEmpty()){
            lastMemory = fileManager.getEpics().get(0).getSuperId();
            return lastMemory;
        }
        return lastMemory;
    }
    public static void main(String[] args) throws IOException {
        new HttpTaskServer(8088).start();
    }
    public void start() {
        System.out.println("Http сервак стартанул PORT = " + port);
        server.start();

    }
    public void stop(){
        server.stop(2);
    }

    public static HttpTaskManager getManager() {
        return fileManager;
    }

    static class TaskHandler implements HttpHandler {
        HashMap<String, Integer> hashLocalDT = new HashMap<>();
        HashMap<String, String> hashBody = new HashMap<>();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int idTask = -1;

            String secondPath = getSecondPostPath(exchange).orElse("");
            String[] arrPath = exchange.getRequestURI().getPath().split("/");
            System.out.println("handle запустился, иду к методам");
            if (exchange.getRequestURI().getRawQuery() != null) {
                idTask = Integer.parseInt(
                        List.of(
                                exchange.getRequestURI().getRawQuery().split("id=")
                        ).get(1)
                );
            }

            if (exchange.getRequestMethod().equals("GET")) {

                if (secondPath.equals("task") && arrPath.length == 3) {
                    if (idTask != -1) {
                        if (fileManager.getTask(idTask) == null) {
                            writeResponse(exchange
                                    , "По этому ID = " + String.valueOf(idTask) + ", Task не найден"
                                    , 404);
                        }
                        System.out.println("/task/task?id=");
                        writeResponse(exchange, gson.toJson(fileManager.getTask(idTask)), 200);
                        return;
                    }
                    if (fileManager.getAllTasks().isEmpty())
                        writeResponse(exchange, "Не могу вернуть пустой список allTasks", 404);
                    System.out.println("/tasks/task");
                    writeResponse(exchange, gson.toJson(fileManager.getAllTasks()), 200);
                    return;
                } // GET /tasks/task DONE and /task/task?id= DONE

                if (secondPath.equals("epic") && arrPath.length == 3) {
                    if (idTask != -1) {
                        if (fileManager.getEpic(idTask) == null) {
                            writeResponse(exchange
                                    , "По этому ID = " + String.valueOf(idTask) + ", Epic не найден"
                                    , 404);
                        }
                        System.out.println("/task/epic?id=");
                        writeResponse(exchange, gson.toJson(fileManager.getEpic(idTask)), 200);
                        return;
                    }
                    if (fileManager.getEpics().isEmpty()) writeResponse(exchange,
                            "Не могу вернуть пустой список getEpics", 404);
                    System.out.println("/tasks/epic");
                    writeResponse(exchange, gson.toJson(fileManager.getEpics()), 200);
                    return;
                } // GET /tasks/epic DONE and /task/epic?id= DONE

                if (secondPath.equals("subtask")
                        && arrPath.length == 3) {
                    if (idTask != -1) {
                        if (fileManager.getSubtask(idTask) == null) {
                            writeResponse(exchange
                                    , "По этому ID = " + String.valueOf(idTask) + ", Subtask не найден"
                                    , 404);
                        }
                        System.out.println("/tasks/subtask");
                        writeResponse(exchange, gson.toJson(fileManager.getSubtask(idTask)), 200);
                        return;
                    }
                    if (fileManager.getSubtasks().isEmpty()) writeResponse(exchange,
                            "Список subtask отсутствует",404);
                    System.out.println("/task/subtask?id=");
                    writeResponse(exchange, gson.toJson(fileManager.getSubtasks()), 200);
                    return;
                }                // GET /tasks/subtask DONE and /task/subtask?id= DONE

                if (secondPath.equals("history") && arrPath.length == 3) {
                    if (fileManager.getHistory() == null) writeResponse(exchange,
                            "История пустая, нечего возвращать",404);
                    System.out.println("/tasks/history");
                    writeResponse(exchange, gson.toJson(fileManager.getHistory()), 200);
                    return;
                }                        // GET /tasks/history DONE

                if (arrPath.length == 2 && arrPath[arrPath.length - 1].equals("tasks")) {
                    if (fileManager.getPrioritizedTasks() == null) writeResponse(exchange,
                            "Приоритетный список пустой, нечего возвращать",404);
                    System.out.println("/tasks/");
                    writeResponse(exchange, gson.toJson(fileManager.getPrioritizedTasks()), 200);
                    return;
                }               // GET /tasks/    DONE

                if (secondPath.equals("subtask") && arrPath[arrPath.length - 1].equals("epic")
                        && arrPath.length == 4) {
                    if (idTask != -1) {
                        if (fileManager.getAllSubtaskEpic(idTask) == null) {
                            writeResponse(exchange
                                    , "По этому GroupId = " + String.valueOf(idTask) + ",  ничего не найдено"
                                    , 404);
                        }
                    }
                    System.out.println("/tasks/subtask/epic?id=");
                    writeResponse(exchange, gson.toJson(fileManager.getAllSubtaskEpic(idTask)), 200);
                    return;
                }                                                            // GET /tasks/subtask/epic?id= DONE

            }
            if (exchange.getRequestMethod().equals("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                if (body.isEmpty()) {
                    writeResponse(exchange, "Пустое тело запроса post", 400);
                    return;
                }
                body = body.replaceAll("\"", "");
                if (secondPath.equals("task") && arrPath.length == 3) {
                    System.out.println("Зашёл в POST");
                    if (body.contains("date")) {
                        for (String text : body.split(",")) {
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

                                if(text.contains("epicId"))putInHashString("epicId",text);
                                if(text.contains("subTaskId"))putInHashString("subTaskId",text);

                                if (text.contains("type")) putInHashString("type", text);
                                if (text.contains("name")) putInHashString("name", text);
                                if (text.contains("description")) putInHashString("description", text);
                            } catch (Exception e) {
                                writeResponse(exchange, "Json был заполнен неккоректно", 400);
                                return;
                            }
                        }
                    } else {
                        body = body.replaceAll("\"", "");
                        String[] first = body.split(",");
                        for (String text : first) {
                            String[] forHashmap = text.trim().replaceAll("\\}", "")
                                    .replaceAll("\\{", "").trim().split(":");

                            hashBody.put(forHashmap[0], forHashmap[1]);
                        }
                    }
                    Task task = null;
                    try {
                        if (hashBody.containsKey("epicId")) {
                            if (hashLocalDT.isEmpty()) {
                                task = new Epic(
                                        hashBody.get("name"),
                                        hashBody.get("description")
                                );
                            } else {
                                task = new Epic(
                                        hashBody.get("name")
                                        , hashBody.get("description")
                                        , LocalDateTime.of(hashLocalDT.get("year")
                                        , hashLocalDT.get("month"), hashLocalDT.get("day")
                                        , hashLocalDT.get("hour"), hashLocalDT.get("minute")
                                        , hashLocalDT.get("second"), hashLocalDT.get("nano"))
                                        , Duration.ofSeconds(hashLocalDT.get("seconds")
                                        , hashLocalDT.get("nanos")).toMinutesPart()
                                );
                            }
                            boolean istrue = fileManager.createTask(task);
                            if (!istrue) {
                                task.minusSumTasks();
                                writeResponse(exchange, "Создания не произошло, скорее всего такой" +
                                        " Epic уже есть", 400);
                                return;
                            }
                            fileManager.updateTask(task); //c психу, потому-что одна не обновляла
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            hashBody.clear();
                            hashLocalDT.clear();
                            System.out.println("/tasks/task/body {task ..}");
                            writeResponse(exchange, "Epic был успешно добавлен", 201);
                            return;
                        } else if (hashBody.containsKey("subTaskId")) {
                            if (hashBody.containsKey("status")) {
                                StatusTask status = null;
                                switch (hashBody.get("status")) {
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
                                        writeResponse(exchange, "Такого статуса не существует", 404);
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
                            if (!istrue) {
                                task.minusSumTasks();
                                if (fileManager.getEpics().isEmpty()) writeResponse(exchange,
                                        "Создания не произошло, нельзя создавать" +
                                        " Subtask без Epic", 400);
                                writeResponse(exchange, "Создания не произошло, скорее всего такой" +
                                        " Subtask уже есть", 400);
                                return;
                            }
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            fileManager.updateTask(task);
                            hashBody.clear();
                            hashLocalDT.clear();
                            System.out.println("/tasks/task/body {task ..}");
                            writeResponse(exchange, "Subtask был успешно добавлен", 201);
                            return;
                        }
                        if (task == null) {
                            writeResponse(exchange, "Json был заполнен неккоректно", 400);
                            return;
                        }

                    } catch (JsonSyntaxException exp) {
                        writeResponse(exchange, "Некорректно введён json", 400);
                        return;
                    }

                }  // POST /tasks/task/body {task ..}  DONE

            }
            if (exchange.getRequestMethod().equals("DELETE")) {

                if (secondPath.equals("task") && arrPath.length == 3 && idTask != -1) {
                    if (!fileManager.deleteEpicOrSubtask(idTask)) {
                        writeResponse(exchange
                                , "Удаления по этому ID = " + String.valueOf(idTask) + ", не произошло"
                                , 404);
                    }
                    System.out.println("/tasks/task/?id=");
                    fileManager.removeHistory(idTask);
                    writeResponse(exchange, "Удаление по id = " + idTask + " произошло успешно", 200);
                    return;
                } // DELETE /tasks/task/?id= DONE

                if (secondPath.equals("task") && arrPath.length == 3) {
                    fileManager.deleteAllTasks();
                    fileManager.clearHistory();
                    System.out.println("/tasks/task/");
                    writeResponse(exchange, "Удаление всего списка произошло успешно", 200);
                    return;
                } //  DELETE /tasks/task/ DONE

            }
            // на delete /tasks/(subtask или epic) просто сил нет уже
            // и как по мне бесполезно, если я делаю реализацию для кнопок, то зачем мне парится?
            // нужная кнопка в нужном месте ведь будет
            writeResponse(exchange, "Nothing was find", 404);
        }



        private void putInHashLDT(String value, String text) {
            String newText = text.substring(text.indexOf(value));
            String[] split = newText.replaceAll("\\s", "")

                    .replaceAll("\\}", "").split(":");
            hashLocalDT.put(split[0], Integer.parseInt(split[1]));
        }

        private void putInHashString(String value, String text) {
            String newText = text.substring(text.indexOf(value));
            String[] split = newText.replaceAll("\\}", "").split(":");
            hashBody.put(split[0], split[1].trim());
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
            if (responseString == null) {
                exchange.sendResponseHeaders(400, 0);
                os.write("Переданный текст равен null".getBytes());
            }
            if (responseString.isBlank()) {
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
