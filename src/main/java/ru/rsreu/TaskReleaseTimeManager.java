package ru.rsreu;

import ru.rsreu.synch.CountDownLatch;
import ru.rsreu.synch.Lock;

import java.util.ArrayList;
import java.util.List;

public class TaskReleaseTimeManager implements Runnable {

    private static final List<CountDownLatch> QUEUE = new ArrayList<>();
    private static final Lock LOCK = new Lock();

    private Long start = null;

    public static void add(CountDownLatch latch) {
        try {
            LOCK.lock();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println(ex.getMessage());
            return;
        }
        try {
            QUEUE.add(latch);
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public void run() {
        try {
            loop();
        } catch (InterruptedException ex) {
            System.out.println("TaskReleaseTimeManager завершил свою работу");
        }
    }

    private void loop() throws InterruptedException {
        long time;
        CountDownLatch countDownLatch;
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            LOCK.lock();
            try {
                if (QUEUE.size() > 0) {
                    countDownLatch = QUEUE.get(0);
                } else {
                    continue;
                }
            } finally {
                LOCK.unlock();
            }

            countDownLatch.await();
            time = System.nanoTime() / 1000;
            if (start == null) {
                start = time;
            }

            System.out.println("Задача завершилась с задержкой = " + (time - start));

            LOCK.lock();
            try {
                QUEUE.remove(0);
            } finally {
                LOCK.unlock();
            }
        }
    }
}
