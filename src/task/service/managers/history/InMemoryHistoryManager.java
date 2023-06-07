package task.service.managers.history;

import task.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new CustomLinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.size() >= 10) {
                remove(0);
            }
            taskHistory.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> nodeByID = taskHistory.occurrences.get(id);
        taskHistory.removeNode(nodeByID);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getTasks();
    }

    private class CustomLinkedList<T extends Task> {
        private Map<Integer, Node<T>> occurrences;
        private Node<T> head;
        private Node<T> tail;

        private int size;

        public CustomLinkedList() {
            occurrences = new HashMap<>();
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
            if (occurrences.containsKey(element.getId())) {
                Node<T> repeatingNode = occurrences.get(element.getId());
                removeNode(repeatingNode);
            }
            final Node<T> t = tail;
            final Node<T> newNode = new Node<>(t, element, null);
            occurrences.put(element.getId(), newNode);
            tail = newNode;
            if (t == null)
                head = newNode;
            else
                t.next = newNode;
            size++;
        }

        public T removeNode(Node<T> node) {
            T element = node.data;
            Node<T> previousNode = node.previous;
            Node<T> nextNode = node.next;
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
            occurrences.remove(node.data.getId());
            node.data = null;
            size--;
            return element;
        }
    }
}
