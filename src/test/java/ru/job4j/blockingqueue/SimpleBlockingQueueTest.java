package ru.job4j.blockingqueue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBlockingQueueTest {
    @Test
    public void whenSimpleBlockingQueueEmpty() throws InterruptedException {
        SimpleBlockingQueue<Integer> simpleBlockingQueue = new SimpleBlockingQueue<>(5);
        Thread first = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    simpleBlockingQueue.offer(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread second = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    simpleBlockingQueue.poll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        assertEquals(0, simpleBlockingQueue.size());
    }
}
