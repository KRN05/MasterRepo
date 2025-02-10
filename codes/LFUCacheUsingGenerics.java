import java.util.*;

class LFUCacheWithGenerics<K, V> {
    private final int capacity;
    private int minFreq;
    private final Map<K, Node<K, V>> nodeMap;
    private final Map<Integer, LinkedHashSet<Node<K, V>>> freqMap;

    public LFUCacheWithGenerics(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.nodeMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    public V get(K key) {
        if (!nodeMap.containsKey(key)) return null;
        Node<K, V> node = nodeMap.get(key);
        updateFrequency(node);
        return node.value;
    }

    public void put(K key, V value) {
        if (capacity == 0) return;

        if (nodeMap.containsKey(key)) {
            Node<K, V> node = nodeMap.get(key);
            node.value = value;
            updateFrequency(node);
        } else {
            if (nodeMap.size() >= capacity) {
                evictLFU();
            }
            Node<K, V> newNode = new Node<>(key, value);
            nodeMap.put(key, newNode);
            freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(newNode);
            minFreq = 1;
        }
    }

    private void updateFrequency(Node<K, V> node) {
        int freq = node.freq;
        freqMap.get(freq).remove(node);
        if (freqMap.get(freq).isEmpty() && freq == minFreq) {
            minFreq++;
        }
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void evictLFU() {
        LinkedHashSet<Node<K, V>> nodes = freqMap.get(minFreq);
        Node<K, V> nodeToEvict = nodes.iterator().next();
        nodes.remove(nodeToEvict);
        if (nodes.isEmpty()) {
            freqMap.remove(minFreq);
        }
        nodeMap.remove(nodeToEvict.key);
    }

    private static class Node<K, V> {
        K key;
        V value;
        int freq;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }
}

// Example Usage
class Main {
    public static void main(String[] args) {
        LFUCacheWithGenerics<String, String> cache = new LFUCacheWithGenerics<>(2);
        cache.put("one", "first");
        cache.put("two", "second");
        System.out.println(cache.get("one")); // Prints "first"
        cache.put("three", "third"); // Evicts "two" (LFU)
        System.out.println(cache.get("two")); // Prints null (not found)
        System.out.println(cache.get("three")); // Prints "third"
    }
}
