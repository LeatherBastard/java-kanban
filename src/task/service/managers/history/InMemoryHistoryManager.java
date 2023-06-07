package task.service.managers.history;

import task.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.occurrences.containsKey(task.getId())) {
                Node<Task> repeatingNode =  taskHistory.occurrences.get(task.getId());
                taskHistory.removeNode(repeatingNode);
            }
            taskHistory.linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> nodeByID = taskHistory.occurrences.get(id);
        taskHistory.removeNode(nodeByID);
    }

    private static class CustomLinkedList<T extends Task> {
        private final Map<Integer, Node<T>> occurrences = new HashMap<>();
        private Node<T> head;
        private Node<T> tail;

        private int size;

        public CustomLinkedList() {
            size = 0;
        }

        public List<T> getTasks() {
            List<T> taskList = new ArrayList<>();
            Node<T> node = head;
            while (size != 0 && node != null) {
                taskList.add(node.data);
                node = node.next;
            }
            return taskList;
        }

        public int size() {
            return size;
        }

        public void linkLast(T element) {



            final Node<T> newNode = new Node<>(tail, element, null);
            occurrences.put(element.getId(), newNode);
            if (tail == null)
                head = newNode;
            else
                tail.next = newNode;
            tail = newNode;
            size++;
        }

        public T removeNode(Node<T> node) {
            T element = node.data;
            final Node<T> previousNode = node.previous;
            final Node<T> nextNode = node.next;
            if (node.previous == null) {
                head = nextNode;
            } else {
                previousNode.next = nextNode;
                node.previous = null;
            }
            if (node.next == null) {
                tail = previousNode;
            } else {
                nextNode.previous = previousNode;
                node.next = null;
            }

            node.data = null;
            size--;
            return element;
        }
    }
}
