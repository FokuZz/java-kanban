package service;

import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> history = new CustomLinkedList<>();



    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(Task task){
        history.remove(task);
    }

    public void setHistory(ArrayList<Task> history) {
        CustomLinkedList<Task> testHistory = new CustomLinkedList<>();
        TreeSet<Task> historySet = new TreeSet<>(Task::compareTime);
        for(Task task: history){
            testHistory.linkLast(task);
            historySet.add(task);
        }
        this.history = testHistory;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public ArrayList<Task> getPrioritizedHistory(){
        TreeSet<Task> historyTree = new TreeSet<>(Task::compareTime);
        historyTree.addAll(history.getTasks());
        return new ArrayList<>(historyTree);
    }
}
class CustomLinkedList<T extends Task>{ ///////////////////////////////////////////// Свой LinkedList c быстрым находдением через HashMap
    private final Map<Integer,T> history = new HashMap<>();
    private Node<T> first;
    private Node<T> last;
    private int size = 0;


    public void linkLast(T task){
        if (history.containsKey(task.getSuperId())){
            for (Node<T> x = last; x != null; x = x.prev) {
                if (task.equals(x.elem)) {
                    removeNode(x);
                }
            }
        } else {
            history.put(task.getSuperId(),task);
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
        if(!history.containsKey(o.getSuperId())) return;
        if (o == null) {
            for (Node<T> x = first; x != null; x = x.next) {
                if (x.elem == null) {
                    removeNode(x);
                }
            }
        } else {
            for (Node<T> x = last; x != null; x = x.prev) {
                if (o.equals(x.elem)) {
                    removeNode(x);
                }
            }
        }
        history.remove(o);
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
