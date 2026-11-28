package ru.job4j.blockingqueue;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

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

    @Test
    public void whenFetchAllThenGetIt() throws InterruptedException {
        final CopyOnWriteArrayList<Integer> buffer = new CopyOnWriteArrayList<>();
        final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        Thread producer = new Thread(
                () -> {
                    IntStream.range(0, 5).forEach(i -> {
                        try {
                            queue.offer(i);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
        );
        producer.start();
        Thread consumer = new Thread(
                () -> {
                    while (queue.size() != 0 || !Thread.currentThread().isInterrupted()) {
                        try {
                            buffer.add(queue.poll());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
        consumer.start();
        producer.join();
        consumer.interrupt();
        consumer.join();
        assertIterableEquals(buffer, Arrays.asList(0, 1, 2, 3, 4), "");
    }
}
