package ru.rsreu;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ThreadPool {

    private final ExecutorService executorService;
    private final Semaphore semaphore;

    public ThreadPool(int numberThread, int numberAvailableThreads) {
        semaphore = new Semaphore(numberAvailableThreads);
        executorService = Executors.newFixedThreadPool(numberThread + 1);
        executorService.execute(new TaskReleaseTimeManager());
    }

    public void runTask(Runnable task, CountDownLatch countDownLatch) {
        TaskReleaseTimeManager.add(countDownLatch);
        executorService.execute(() -> {
            try {
                semaphore.acquire();
                task.run();
                semaphore.release();
            } catch (InterruptedException ignored) {
                System.out.println("Поток завершился на ожидании семафора");
            }
        });
    }

    public void stopAll() {
        executorService.shutdownNow();
    }
}
