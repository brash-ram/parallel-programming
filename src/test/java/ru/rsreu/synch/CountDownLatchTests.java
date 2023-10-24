package ru.rsreu.synch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    void testCountDownLatchReset() throws InterruptedException {
        int count = 1;
        CountDownLatch latch = new CountDownLatch(count);

        Thread testThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        assertEquals(count, latch.getCount());

        testThread.start();
        latch.await();

        assertFalse(testThread.isAlive());
        assertEquals(0, latch.getCount());
    }
}
