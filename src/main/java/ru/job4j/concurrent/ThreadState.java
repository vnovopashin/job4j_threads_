package ru.job4j.concurrent;

public class ThreadState {
    public static void main(String[] args) {
        Thread first = new Thread(
                () -> {
                }
        );
        Thread second = new Thread(
                () -> {
                }
        );
        System.out.printf("NEW Threads\nfirst thread: %s \nsecond thread: %s\n\n",
                first.getState(), second.getState());
        first.start();
        second.start();
        while (first.getState() != Thread.State.TERMINATED
                || second.getState() != Thread.State.TERMINATED) {
            System.out.printf("RUNNABLE Threads\nfirst thread: %s \nsecond thread: %s\n\n",
                    first.getState(), second.getState());
        }
        System.out.printf("TERMINATED Treads\nfirst thread: %s \nsecond thread: %s\n",
                first.getState(), second.getState());

        System.out.print(Thread.currentThread().getName());
    }
}
