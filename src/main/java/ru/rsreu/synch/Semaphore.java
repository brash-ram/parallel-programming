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

    public boolean tryAcquire(int timeoutMillis) throws InterruptedException {
        synchronized(LOCK) {
            if (permits > 0) {
                permits--;
                return true;
            }

            if (timeoutMillis <= 0) {
                return false;
            }

            long endTime = System.currentTimeMillis() + timeoutMillis;
            long remainingTime = timeoutMillis;

            while (remainingTime > 0) {
                LOCK.wait(remainingTime);

                if (permits > 0) {
                    permits--;
                    return true;
                }

                remainingTime = endTime - System.currentTimeMillis();
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

    public void release(int permits) {
        synchronized(LOCK) {
            this.permits += permits;
            LOCK.notify();
        }
    }

    public int availablePermits() {
        return permits;
    }
}