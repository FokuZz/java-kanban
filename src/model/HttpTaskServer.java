package model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import service.FileBackedTasksManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {
    Gson gson = new Gson();
    private static int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    static FileBackedTasksManager fileManager;

    public static void main(String[] args) throws IOException {
        fileManager = Managers.getFileManager();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/task", new TaskHandler());
        httpServer.start();
        System.out.println("Http сервак стартанул PORT = "+PORT);
    }

    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("1");
            if (exchange.getRequestMethod() == "GET"){
                System.out.println("2");
                if(getSecondPostId(exchange).orElse ("").equals("task")){ // GET /task/task
                    System.out.println("3");
                    writeResponse(exchange,fileManager.getAllTasks().toString(),200);
                }
                if(exchange.getRequestURI().getPath().split("/").length == 2){
                    System.out.println("4");
                    writeResponse(exchange,fileManager.getPrioritizedTasks().toString(),200);
                }
            }
            if (exchange.getRequestMethod() == "POST"){

            }
            writeResponse(exchange,"кхм, ничего не нашёл сори",404);
        }

        private Optional<String> getSecondPostId(HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(pathParts[2]);
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}
