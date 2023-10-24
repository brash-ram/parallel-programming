package ru.rsreu.synch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CountDownLatchTests {

    @Test
    void testCountDownLatchAwait() throws InterruptedException {
        int count = 3;
        CountDownLatch latch = new CountDownLatch(count);

        Thread testThread1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread testThread2 = new Thread(() -> {
            try {
                Thread.sleep(2000);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread testThread3 = new Thread(() -> {
            try {
                Thread.sleep(3000);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        testThread1.start();
        testThread2.start();
        testThread3.start();

        latch.await();
        assertFalse(testThread1.isAlive());
        assertFalse(testThread2.isAlive());
        assertFalse(testThread3.isAlive());
        assertEquals(0, latch.getCount());
    }

    @Test
    void testCountDownLatchCount() throws InterruptedException {
        int count = 3;
        CountDownLatch latch = new CountDownLatch(count);

        assertEquals(count, latch.getCount());

        latch.countDown();

        assertEquals(count - 1, latch.getCount());
    }

    @Test
    void testCountDownLatchTimeout() throws InterruptedException {
        int count = 1;
        CountDownLatch latch = new CountDownLatch(count);

        Thread testThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
            }
        });

        testThread.start();
        Thread.sleep(100);
        latch.await(100);

        assertTrue(testThread.isAlive());
        assertNotEquals(0, latch.getCount());
        testThread.interrupt();
    }
}
