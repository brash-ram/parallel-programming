package ru.rsreu;

import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final Object lock = new Object();

    private static class LazyInitStorage {
        static final List<Double> STORAGE_LIST = new ArrayList<>();
    }

    public static void add(Double value) {
        synchronized (lock) {
            LazyInitStorage.STORAGE_LIST.add(value);
            lock.notify();
        }
        try {
            ProgressBar.getInstance().setValueAndUpdateProgress(value);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static double get() throws InterruptedException {
        synchronized (lock) {
            while (LazyInitStorage.STORAGE_LIST.size() < 1) {
                lock.wait();
            }
            double value = LazyInitStorage.STORAGE_LIST.get(0);
            LazyInitStorage.STORAGE_LIST.remove(0);
            return value;
        }
    }
}
