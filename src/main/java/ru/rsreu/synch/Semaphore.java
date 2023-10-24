package ru.rsreu.synch;

public class Semaphore {
    private int permits;
    private static final Object LOCK = new Object();

    public Semaphore(int permits) {
        this.permits = permits;
    }

    public void acquire() throws InterruptedException {
        synchronized(LOCK) {
            while (permits == 0) {
                LOCK.wait();
            }
            permits--;
        }
    }

    public boolean tryAcquire() {
        synchronized(LOCK) {
            if (permits > 0) {
                permits--;
                return true;
            }
            return false;
        }
    }

    public void release() {
        synchronized(LOCK) {
            permits++;
            LOCK.notify();
        }
    }

    public int availablePermits() {
        return permits;
    }
}