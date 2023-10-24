package ru.rsreu.synch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LockTests {

    @Test
    void testLock() throws InterruptedException {
        Lock lock = new Lock();

        lock.lock();

        assertFalse(lock.tryLock());

        lock.unlock();
        assertTrue(lock.tryLock());
    }

    @Test
    void testTryLockWithTimeout() throws InterruptedException {
        Lock lock = new Lock();
        int timeout = 1000;

        assertTrue(lock.tryLock());

        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
            } catch (InterruptedException e) {
                return;
            }
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        t1.start();
        Thread.sleep(100);
        assertFalse(lock.tryLock());
        lock.unlock();

        t1.join();
        assertTrue(lock.tryLock());
    }
}