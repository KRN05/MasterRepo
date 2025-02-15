import java.util.*;

class LRUCacheWithGenerics<K, V> {
    private final int capacity;
    private final Map<K, V> keyValueMap;   // Stores key-value pairs
    private final Map<K, Node<K, V>> keyNodeMap; // Stores key-node references
    private final DoublyLinkedList<K, V> dll; // Keeps LRU order

    public LRUCacheWithGenerics(int capacity) {
        this.capacity = capacity;
        this.keyValueMap = new HashMap<>();
        this.keyNodeMap = new HashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    public V get(K key) {
        if (!keyValueMap.containsKey(key)) return null;

        // Move accessed node to the front (Most Recently Used)
        Node<K, V> node = keyNodeMap.get(key);
        dll.moveToHead(node);
        return keyValueMap.get(key);
    }

    public void put(K key, V value) {
        if (keyValueMap.containsKey(key)) {
            // Update value and move node to front
            keyValueMap.put(key, value);
            Node<K, V> node = keyNodeMap.get(key);
            dll.moveToHead(node);
        } else {
            if (keyValueMap.size() >= capacity) {
                // Remove Least Recently Used (LRU) node
                Node<K, V> lruNode = dll.removeTail();
                keyValueMap.remove(lruNode.key);
                keyNodeMap.remove(lruNode.key);
            }
            // Insert new node at front
            Node<K, V> newNode = new Node<>(key, value);
            dll.addToHead(newNode);
            keyValueMap.put(key, value);
            keyNodeMap.put(key, newNode);
        }
    }

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class DoublyLinkedList<K, V> {
        private final Node<K, V> head, tail;

        DoublyLinkedList() {
            head = new Node<>(null, null);
            tail = new Node<>(null, null);
            head.next = tail;
            tail.prev = head;
        }

        void addToHead(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        void moveToHead(Node<K, V> node) {
            removeNode(node);
            addToHead(node);
        }

        Node<K, V> removeTail() {
            Node<K, V> lastNode = tail.prev;
            removeNode(lastNode);
            return lastNode;
        }

        void removeNode(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public static void main(String[] args) {
        LRUCacheWithGenerics<Integer, String> cache = new LRUCacheWithGenerics<>(3);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        System.out.println(cache.get(1)); // A
        cache.put(4, "D"); // Removes key 2 (LRU)
        System.out.println(cache.get(2)); // null (not found)
        System.out.println(cache.get(3)); // C
    }
}
