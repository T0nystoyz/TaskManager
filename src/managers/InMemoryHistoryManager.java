package managers;


import tasks.Task;
import utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node<Task>> nodesWithId = new HashMap<>();
    private final MyLinkedList<Task> history = new MyLinkedList<>();

    @Override
    public void add(Task task) {
        Node<Task> node = nodesWithId.get(task.getId());
        if (node != null) {
            history.removeNode(node);
        }
        history.linkLast(task);
        nodesWithId.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodesWithId.get(id);
        if (node != null) {
            history.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class MyLinkedList<T> {
        protected Node<T> head;
        protected Node<T> tail;

        public void linkLast(T task) {
            Node<T> prevTail = tail;
            Node<T> newNode = new Node<>(tail, task, null);

            if (prevTail != null) {
                prevTail.setNext(newNode);
                head = prevTail;
                prevTail.setPrev(null);
            } else {
                head = newNode;
            }
        }

        public List<T> getTasks() {
            List<T> list = new ArrayList<>();
            Node<T> start = head;
            while (start != null) {
                list.add(0, start.getTask());
                start = start.getNext();
            }
            return list;
        }

        void removeNode(Node<T> node) {
            Node<T> prev = node.getPrev();
            Node<T> next = node.getNext();
            if (prev != null) {
                prev.setNext(next);
            } else {
                next = head;
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                tail = prev;
            }
        }
    }
}