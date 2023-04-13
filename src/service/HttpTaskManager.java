package service;

import client.KVTaskClient;
import com.google.gson.Gson;
import model.Epic;
import model.HttpLoadHelper;
import model.Subtask;
import model.Task;
import service.exceptions.ManagerSaveException;

import java.net.URI;
import java.util.ArrayList;

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
