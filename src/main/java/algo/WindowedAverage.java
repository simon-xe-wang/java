package algo;

import java.util.HashMap;
import java.util.Map;

public class WindowedAverage {

    long windowSize;

    long runningSum;
    long count;

    Map<String, Node> keyToNode = new HashMap<>();
    DoublyLinkedList nodes = new DoublyLinkedList();

    public WindowedAverage(long windowSize) {
        this.windowSize = windowSize;
    }

    public long get(String key) {
        Node node = keyToNode.get(key);
        return node == null ? null : node.value;
    }

    public void put(String key, long value, long timestamp) {
        // remove old key if exists(keyToNode, nodes, runningSum)
        if (keyToNode.containsKey(key)) {
            Node oldNode = keyToNode.remove(key);
            runningSum -= oldNode.value;
            count--;
            nodes.remove(oldNode);
        }

        // add key
        Node newNode = new Node(key, value, timestamp);
        keyToNode.put(key, newNode);
        runningSum += value;
        count++;
        nodes.append(newNode);
    }

    public double getAvg(long timestamp) {
        // remove all expired nodes from nodes, keyToNode, and runningSum
        // calc avg and return

        Node curNode = nodes.head.next;
        while (curNode != nodes.tail) {
            if (curNode.isExpired(timestamp)) {
                nodes.remove(curNode);
                keyToNode.remove(curNode.key);
                runningSum -= curNode.value;
                count--;
                System.out.println("remove a expired key " + curNode);

                curNode = curNode.next;
            } else {
                break;
            }
        }

        return count != 0 ? runningSum*1.0 / count : 0;
    }

    class Node {
        String key;
        long value;
        long updateTime;

        Node prev;
        Node next;

        public Node(String key, long value, long updateTime) {
            this.key = key;
            this.value = value;
            this.updateTime = updateTime;
        }

        boolean isExpired(long timestamp) {
            System.out.println("node " + key + " live " + (timestamp - updateTime) + " ms") ;
            return timestamp  - updateTime > windowSize;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    ", updateTime=" + updateTime +
                    '}';
        }
    }

    class DoublyLinkedList {
        Node head = new Node("", 0, 0);
        Node tail = new Node("", 0, 0);

        public DoublyLinkedList() {
            head.next = tail;
            tail.prev = head;
        }

        public void remove(Node node) {

            node.prev.next = node.next;
            node.next.prev = node.prev;

        }

        public void append(Node node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;

        }
    }
}
