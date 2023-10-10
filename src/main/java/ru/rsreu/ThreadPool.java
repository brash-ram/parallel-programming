package ru.rsreu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private final ExecutorService executorService;

    public ThreadPool(int numberThread) {
        executorService = Executors.newFixedThreadPool(numberThread);
    }

    public void runTask(Runnable task) {
        executorService.execute(task);
    }

    public void stopAll() {
        executorService.shutdownNow();
    }
}
