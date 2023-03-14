package client;

import com.google.gson.Gson;
import service.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String apiToken = "";
    private URI uri = URI.create("error");
    Gson gson = new Gson();

    String json = "";
    public KVTaskClient(URI uri) throws ManagerSaveException {
        this.uri = uri;
        try {
            HttpResponse<String> response = getResponse("GET","/register");
            if (response == null) throw new ManagerSaveException("Что-то не так с загрузкой клиента KVTask");
            if (response.statusCode() != 200){
                System.out.println("Что-то пошло не так при загрузке конструктора KVTASKCLIENT\n" +
                        "Код состояния "+ response.statusCode()+"\n" +
                        "Ответ "+ response.body());
            };
            apiToken = response.body();

            System.out.println("Код состояния " + response.statusCode());
            System.out.println("Успешно зарегистрировался! API = " + apiToken);

        } catch (IOException | InterruptedException exp){
            throw new ManagerSaveException("Ошибка загрузки url адреса=" + uri);
        }
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void put(String json){
        try {
            this.json = json;
            HttpResponse<String> response = getResponse("POST","/save/"+apiToken+"?API_TOKEN="+apiToken);
            if (response == null) throw new IOException();
            if(response.statusCode() >= 200 && response.statusCode() < 300){
                System.out.println("Успешно сохранилось");
            }else {
                System.out.println("Что-то пошло не так при сохранении");
                return;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Произошла ошибка при методе put!", e);
        }
    }

    public String load(){
        try {
            HttpResponse<String> response = getResponse("GET","/load?API_TOKEN="+apiToken);
            if (response == null || response.body() == null) throw new IOException();
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при методе put!");
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> getResponse(String method,String url) throws IOException, InterruptedException {
        HttpRequest request = null;

        switch (method){
            case "GET":
                try {
                    request = HttpRequest.newBuilder()
                            .GET()
                            .version(HttpClient.Version.HTTP_1_1)
                            .uri(URI.create(uri + url))
                            .build();

                } catch (Exception e){
                    System.out.println("Что-то не так при создании запроса у метода GET\nURI = "+URI.create(uri + url));
                }

                return HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
            case "POST":
                try {
                    if (json.isEmpty()) throw new IOException("Пустое тело запроса");
                request = HttpRequest.newBuilder()
                        .uri(URI.create(uri+url))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .version(HttpClient.Version.HTTP_1_1)
                        .header("Content-Type", "application/json")
                        .build();

                } catch (Exception e){
                     System.out.println("Что-то не так при создании запроса у метода POST\nURI = "+URI.create(uri + url)+" "
                     +"Body = "+json);
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
                return  response;
            default:
                System.out.println("Такого метода нет в клиенте");
                return null;
        }
    }
}
