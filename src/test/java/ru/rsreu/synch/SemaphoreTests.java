package ru.rsreu.synch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemaphoreTests {

    @Test
    void testSemaphoreAcquire() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();

        assertEquals(0, semaphore.availablePermits());
    }

    @Test
    void testSemaphoreRelease() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        semaphore.release();

        assertEquals(1, semaphore.availablePermits());
    }

    @Test
    void testSemaphoreTryAcquire() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);

        assertTrue(semaphore.tryAcquire());
        assertFalse(semaphore.tryAcquire());
    }

    @Test
    void testSemaphoreTryAcquireWithTimout() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);

        Thread testThread = new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(1000);
                semaphore.release();
            } catch (InterruptedException e) {
            }
        });

        testThread.start();
        Thread.sleep(100);

        assertFalse(semaphore.tryAcquire(100));
        testThread.interrupt();
    }
}