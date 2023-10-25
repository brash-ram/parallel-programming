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
                System.out.println(e.getMessage());
            }
        });

        Thread testThread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        Thread testThread3 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        testThread1.start();
        testThread2.start();
        testThread3.start();

        latch.await();
        assertEquals(0, latch.getCount());
    }

    @Test
    void testCountDownLatchCount() {
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
                System.out.println(e.getMessage());
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