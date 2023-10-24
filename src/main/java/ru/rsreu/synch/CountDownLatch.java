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

    public boolean tryAwait() {
        synchronized(LOCK) {
            return count == 0;
        }
    }

    public int getCount() {
        return count;
    }
}