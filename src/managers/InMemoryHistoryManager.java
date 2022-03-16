package managers;


import tasks.Task;
import utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static final MyLinkedList history = new MyLinkedList();
    private final Map<Integer, Node<Task>> nodesWithId = history.getNodesWithId();

    @Override
    public void add(Task task) {
        Node<Task> node = nodesWithId.get(task.getId());
        if (node != null) {
            history.removeNode(node);
            nodesWithId.remove(task.getId());
            history.linkLast(task);
        } else {
            history.linkLast(task);
            //nodesWithId.put(task.getId(), node);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodesWithId.get(id);
        if (node != null) {
            history.removeNode(node);
            nodesWithId.remove(id);
        }

    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class MyLinkedList {
        private final Map<Integer, Node<Task>> nodesWithId = new HashMap<>();
        protected Node<Task> head;
        protected Node<Task> tail;

        public void linkLast(Task task) {
            Node<Task> prevTail = tail;
            Node<Task> newNode = new Node<>(tail, task, null);
            nodesWithId.put(task.getId(), newNode);
            tail = newNode;

            if (prevTail != null) {
                prevTail.setNext(newNode);
            } else {
                head = newNode;
            }
        }

        public List<Task> getTasks() {
            List<Task> list = new ArrayList<>();
            Node<Task> start = head;
            while (start != null) {
                list.add(start.getTask());
                start = start.getNext();
            }
            return list;
        }

        void removeNode(Node<Task> node) {
            Node<Task> prev = node.getPrev();
            Node<Task> next = node.getNext();
            if (prev != null) {
                prev.setNext(next);
            } else {
                head = next;
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                tail = prev;
            }
        }

        public Map<Integer, Node<Task>> getNodesWithId() {
            return nodesWithId;
        }
    }
}