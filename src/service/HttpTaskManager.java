package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import client.KVTaskClient;
import server.HttpTaskServer;
import service.exceptions.ManagerSaveException;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static KVTaskClient client = null;
    private static Gson gson;
    private HttpLoadHelper send = null;



    public HttpTaskManager(String uri) throws ManagerSaveException {
        client = new KVTaskClient(URI.create(uri));
        gson = new Gson();
    }

    private HttpTaskManager(ArrayList<Epic> epics, ArrayList<Subtask> subtasks, int count, HistoryManager historyManager, URI uri) throws ManagerSaveException {
        setEpics(epics);
        setSubtasks(subtasks);
        setCounter(count);
        setHistoryManager(historyManager);
    }

    @Override
    public void save() {
        ArrayList<Integer> history = new ArrayList<>();
        if (!this.getHistoryManager().isEmpty()) {
            for (Task task : getHistory()) {
                history.add(task.getSuperId());
            }
            send = new HttpLoadHelper(
                    getEpics(),
                    getSubtasks(),
                    getCounter(),
                    history
            );
        } else {
            send = new HttpLoadHelper(
                    getEpics(),
                    getSubtasks(),
                    getCounter()
            );
        }


        client.put(gson.toJson(send, HttpLoadHelper.class));
        super.save();
    }


    public static HttpTaskManager loadFromFile(URI uri) throws ManagerSaveException {
        client = new KVTaskClient(uri);
        gson = new Gson();
        String s = client.load();
        HttpLoadHelper o = gson.fromJson(s, HttpLoadHelper.class);
        if(o == null) return  new HttpTaskManager(uri.toString());
        return new HttpTaskManager(o.getEpics(), o.getSubtasks(), o.getCount(), o.getHistoryManager(),uri);
    }

    public void setApi(String api) {
        client.setApiToken(api);
    }
}
