package ru.job4j.barrier;

public class CountBarrierRunner {
    public static void main(String[] args) {
        CountBarrier barrier = new CountBarrier(2);

        Thread one = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " started");
            barrier.await();
        }, "One thread");

        Thread two = new Thread(() -> {
            barrier.count();
            barrier.count();
            System.out.println(Thread.currentThread().getName() + " started");
        }, "Two thread");

        one.start();
        two.start();
    }
}
