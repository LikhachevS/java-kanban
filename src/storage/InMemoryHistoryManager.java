package storage;

import interfaces.HistoryManager;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history;
    Node first;
    Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private void linkLast(Task task) {
        final Node l = last;
        Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node nodePrev = node.prev;
            Node nodeNext = node.next;
            if (nodePrev != null) {
                nodePrev.next = nodeNext;
            } else {
                first = nodeNext;
            }
            if (nodeNext != null) {
                nodeNext.prev = nodePrev;
            } else {
                last = nodePrev;
            }
            history.remove(node.item.getId());
        }
    }

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.item);
            current = current.next;
        }
        return list;
    }

}
