import java.util.*;

class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map;
    private final DoublyLinkedList dll;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.dll = new DoublyLinkedList();
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;

        Node node = map.get(key);
        dll.moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            dll.moveToHead(node);
        } else {
            if (map.size() >= capacity) {
                int removedKey = dll.removeTail();
                map.remove(removedKey);
            }
            Node newNode = new Node(key, value);
            dll.addToHead(newNode);
            map.put(key, newNode);
        }
    }

    private static class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class DoublyLinkedList {
        private final Node head, tail;

        DoublyLinkedList() {
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
        }

        void addToHead(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        void moveToHead(Node node) {
            removeNode(node);
            addToHead(node);
        }

        int removeTail() {
            Node lastNode = tail.prev;
            removeNode(lastNode);
            return lastNode.key;
        }

        void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        System.out.println(cache.get(1)); // 100
        cache.put(4, 400); // Removes key 2 (LRU)
        System.out.println(cache.get(2)); // -1 (not found)
        System.out.println(cache.get(3)); // 300
    }
}
