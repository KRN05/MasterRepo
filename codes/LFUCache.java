import java.util.*;

class LFUCache {
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> nodeMap;
    private final Map<Integer, LinkedHashSet<Node>> freqMap;
    
    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.nodeMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }
    
    public int get(int key) {
        if (!nodeMap.containsKey(key)) return -1;
        Node node = nodeMap.get(key);
        updateFrequency(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        if (capacity == 0) return;
        
        if (nodeMap.containsKey(key)) {
            Node node = nodeMap.get(key);
            node.value = value;
            updateFrequency(node);
        } else {
            if (nodeMap.size() >= capacity) {
                evictLFU();
            }
            Node newNode = new Node(key, value);
            nodeMap.put(key, newNode);
            freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(newNode);
            minFreq = 1;
        }
    }
    
    private void updateFrequency(Node node) {
        int freq = node.freq;
        freqMap.get(freq).remove(node);
        if (freqMap.get(freq).isEmpty() && freq == minFreq) {
            minFreq++;
        }
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }
    
    private void evictLFU() {
        LinkedHashSet<Node> nodes = freqMap.get(minFreq);
        Node nodeToEvict = nodes.iterator().next();
        nodes.remove(nodeToEvict);
        if (nodes.isEmpty()) {
            freqMap.remove(minFreq);
        }
        nodeMap.remove(nodeToEvict.key);
    }
    
    private static class Node {
        int key, value, freq;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }
}

// Example Usage
class LFUCacheMain {
    public static void main(String[] args) {
        LFUCache cache = new LFUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // returns 1
        cache.put(3, 3); // evicts key 2
        System.out.println(cache.get(2)); // returns -1 (not found)
        System.out.println(cache.get(3)); // returns 3
    }
}
