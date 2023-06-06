package task.service.managers.history;

import task.model.Task;

public class Node<T extends Task> {
    protected T data;
    protected Node<T> previous;
    protected Node<T> next;

    public Node(Node<T> previous, T data, Node<T> next) {
        this.previous = previous;
        this.data = data;
        this.next = next;
    }
}
