package ru.rsreu.synch;

public class CountDownLatch {
    private int count;
    private static final Object LOCK = new Object();

    public CountDownLatch(int count) {
        this.count = count;
    }

    public void countDown() {
        synchronized(LOCK) {
            count--;
            if (count == 0) {
                LOCK.notifyAll();
            }
        }
    }

    public void await() throws InterruptedException {
        synchronized(LOCK) {
            while (count > 0) {
                LOCK.wait();
            }
        }
    }
    public void await(int timeoutMillis) throws InterruptedException {
        synchronized(LOCK) {
            if (timeoutMillis <= 0) {
                return;
            }

            long endTime = System.currentTimeMillis() + timeoutMillis;
            long remainingTime = timeoutMillis;

            while (remainingTime > 0) {
                LOCK.wait(remainingTime);
                remainingTime = endTime - System.currentTimeMillis();
            }
        }
    }


    public boolean tryAwait() {
        synchronized(LOCK) {
            return count == 0;
        }
    }

    public int getCount() {
        return count;
    }
}