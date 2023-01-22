package model;

public class Node<T> {
    public T elem;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T elem, Node<T> next) {
        this.elem = elem;
        this.next = next;
        this.prev = prev;
    }
}
