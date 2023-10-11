package ru.rsreu;

import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final Object LOCK = new Object();

    private static class DoubleCheckInitStorage {
        private static List<Double> STORAGE_LIST;

        private static final Object LOCK_STORAGE = new Object();

        private static List<Double> getStorage() {
            if (STORAGE_LIST == null) {
                synchronized (LOCK_STORAGE) {
                    if (STORAGE_LIST == null) {
                        STORAGE_LIST = new ArrayList<>();
                    }
                }
            }
            return STORAGE_LIST;
        }
    }

    public static void add(Double value) {
        synchronized (LOCK) {
            DoubleCheckInitStorage.getStorage().add(value);
            LOCK.notify();
        }
    }

    public static double get() throws InterruptedException {
        synchronized (LOCK) {
            while (DoubleCheckInitStorage.getStorage().size() < 1) {
                LOCK.wait();
            }
            double value = DoubleCheckInitStorage.getStorage().get(0);
            DoubleCheckInitStorage.getStorage().remove(0);
            return value;
        }
    }
}
