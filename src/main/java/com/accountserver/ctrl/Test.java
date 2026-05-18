package com.accountserver.ctrl;

public class Test {


    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // 链式队列类
    static class LinkedQueue {
        private Node front; // 队列头
        private Node rear;  // 队列尾
        private int size;   // 队列大小

        public LinkedQueue() {
            this.front = null;
            this.rear = null;
            this.size = 0;
        }

        // 入队操作
        public void enqueue(int data) {
            Node newNode = new Node(data);
            if (rear == null) {
                front = newNode;
                rear = newNode;
            } else {
                rear.next = newNode;
                rear = newNode;
            }
            size++;
        }

        // 出队操作
        public int dequeue() {
            if (isEmpty()) {
                throw new IllegalStateException("Queue is empty");
            }
            int data = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            size--;
            return data;
        }

        // 检查队列是否为空
        public boolean isEmpty() {
            return size == 0;
        }

        // 获取队列大小
        public int size() {
            return size;
        }
    }

    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        System.out.println("Size: " + queue.size()); // 输出: Size: 3
        System.out.println("Dequeue: " + queue.dequeue()); // 输出: Dequeue: 1
        System.out.println("Dequeue: " + queue.dequeue()); // 输出: Dequeue: 2
        System.out.println("Dequeue: " + queue.dequeue()); // 输出: Dequeue: 3
        System.out.println("Is Empty: " + queue.isEmpty()); // 输出: Is Empty: false
        System.out.println("Size: " + queue.size()); // 输出: Size: 2
    }
}
