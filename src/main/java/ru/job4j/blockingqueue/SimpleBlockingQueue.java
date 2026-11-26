package ru.job4j.blockingqueue;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

@ThreadSafe
public class SimpleBlockingQueue<T> {
    @GuardedBy("this")
    private final Queue<T> queue = new LinkedList<>();

    private final int queueMaxSize;

    public SimpleBlockingQueue(int queueMaxSize) {
        this.queueMaxSize = queueMaxSize;
    }

    public synchronized void offer(T value) throws InterruptedException {
        while (queue.size() == queueMaxSize) {
            wait();
        }
        queue.offer(value);
        notify();
    }

    public synchronized T poll() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T rsl = queue.poll();
        notify();
        return rsl;
    }

    public synchronized int size() {
        return queue.size();
    }
}
