package ru.rsreu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskReleaseTimeManager implements Runnable {

    private static final List<CountDownLatch> QUEUE = new ArrayList<>();
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Lock READ_LOCK = LOCK.readLock();
    private static final Lock WRITE_LOCK = LOCK.writeLock();

    private Long start = null;

    public static void add(CountDownLatch latch) {
        WRITE_LOCK.lock();
        QUEUE.add(latch);
        WRITE_LOCK.unlock();
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

            READ_LOCK.lock();
            if (QUEUE.size() > 0) {
                countDownLatch = QUEUE.get(0);
            } else {
                READ_LOCK.unlock();
                continue;
            }
            READ_LOCK.unlock();

            countDownLatch.await();
            time = System.nanoTime() / 1000;
            if (start == null) {
                start = time;
            }

            System.out.println("Задача завершилась с задержкой = " + (time - start));

            WRITE_LOCK.lock();
            QUEUE.remove(0);
            WRITE_LOCK.unlock();
        }
    }
}
