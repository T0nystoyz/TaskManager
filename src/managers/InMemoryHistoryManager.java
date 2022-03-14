package managers;


import tasks.Task;
import utils.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    public static class TasksNode<T> {
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T task) {
            Node<T> prevTail = tail;
            Node <T> newNode = new Node<> (tail, task, null);
            tail = newNode;
            if (prevTail == null) {
                prevTail.next = newNode;
            } else {
                prevTail.prev = newNode;
            }
        }

        public List<Task> getTasks() {
            List<Task> list = new ArrayList<>();
            Node node = head;
            while (node != null) {
                list.add((Task) node.task);
                node = node.next;
            }
            return list;
        }

        void removeNode(Node<T> node) {
            Node<T> prev = node.getPrev();
            Node<T> next = node.getNext();
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
            if (tail == node) {
                tail = prev;
            }
        }

    }

    private final Map<Integer, Node<Task>> nodesWithId = new HashMap<>();
    private final TasksNode<Task> history = new TasksNode<>();

    @Override
    public void add(Task task) {
        Node<Task> n = nodesWithId.get(task.getId());
        if (n != null) {
            history.removeNode(n);
        }
        history.linkLast(task);
        nodesWithId.put(task.getId(), history.tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> nodes = nodesWithId.get(id);
        if (nodes != null) {
            history.removeNode(nodes);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}