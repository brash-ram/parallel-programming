package ru.rsreu.synch;

public class Lock {
    private boolean isLocked = false;

    private static final Object LOCK = new Object();

    public void lock() throws InterruptedException {
        synchronized(LOCK) {
            while (isLocked) {
                LOCK.wait();
            }
            isLocked = true;
        }
    }

    public void unlock() {
        synchronized(LOCK) {
            isLocked = false;
            LOCK.notify();
        }
    }

    public boolean tryLock() {
        synchronized(LOCK) {
            if (!isLocked) {
                isLocked = true;
                return true;
            }
            return false;
        }
    }
}