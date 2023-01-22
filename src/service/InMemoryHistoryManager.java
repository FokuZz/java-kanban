package service;

import model.Node;
import model.Task;
import service.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> historyLinkedList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        historyLinkedList.linkLast(task);
    }

    @Override
    public void remove(Task task){
        historyLinkedList.remove(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyLinkedList.getTasks();
    }
}
class CustomLinkedList<T extends Task>{ ///////////////////////////////////////////// Свой LinkedList c быстрым находдением через HashMap
    Map<Integer,T> historyMap = new HashMap<>();
    Node<T> first;
    Node<T> last;
    int size = 0;

    public void linkLast(T task){
        if (historyMap.containsKey(task.getSuperId())){
            for (Node<T> x = last; x != null; x = x.prev) {
                if (task.equals(x.elem)) {
                    removeNode(x);
                }
            }
        } else {
            historyMap.put(task.getSuperId(),task);
        }
        final Node<T> l = last;
        final Node<T> newNode = new Node<T>(l,task,null);
        last = newNode;
        if (l == null)
            last = newNode;
        else
            l.next = newNode;
        size++;
    }

    public ArrayList<T> getTasks(){
        ArrayList<T> returnList = new ArrayList<>(size);
        for (Node<T> x = last; x != null; x = x.prev) {
            returnList.add(x.elem);
        }
        return returnList;
    }

    public void remove(T o){
        if(!historyMap.containsKey(o.getSuperId())) return;
        if (o == null) {
            for (Node<T> x = first; x != null; x = x.next) {
                if (x.elem == null) {
                    removeNode(x);
                }
            }
        } else {
            for (Node<T> x = first; x != null; x = x.next) {
                if (o.equals(x.elem)) {
                    removeNode(x);
                }
            }
        }
        historyMap.remove(o);
    }

    private T removeNode(Node<T> x) {
        final T element = x.elem;
        final Node<T> next = x.next;
        final Node<T> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.elem = null;
        size--;
        return element;
    }
}
