package ru.rsreu;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private final ExecutorService executorService;

    public ThreadPool(int numberThread) {
        executorService = Executors.newFixedThreadPool(numberThread + 1);
        executorService.execute(new TaskReleaseTimeManager());
    }

    public void runTask(Runnable task, CountDownLatch countDownLatch) {
        TaskReleaseTimeManager.add(countDownLatch);
        executorService.execute(task);
    }

    public void stopAll() {
        executorService.shutdownNow();
    }
}
