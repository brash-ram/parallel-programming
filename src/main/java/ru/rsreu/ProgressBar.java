package ru.rsreu;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProgressBar {

    private static ProgressBar instance;

    private static final Lock LOCK = new ReentrantLock();

    private double valuePrev;
    private double value;
    private boolean isDirty = false;

    private static final int STEP_INFO = 20;
    private static final int PERCENT = 100 / STEP_INFO;

    private int hx = 1;
    private Double h0;
    private double h;

    public static ProgressBar getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public static ProgressBar getInstance(double h0) {
        if (instance == null) {
            LOCK.lock();
            instance = new ProgressBar(h0);
            LOCK.unlock();
        }
        return instance;
    }

    public ProgressBar(double h0) {
        this.h0 = h0;
    }

    public void setValue(double value) {
        LOCK.lock();
        this.value = value;
        while (h * h0 > Math.abs(value - valuePrev) && hx != 1) {
            h *= h0;
            hx++;
        }
        LOCK.unlock();
    }

    public void setH0(double h0) {
        this.h0 = h0;
    }

    public int getProgress() {
        if (h0 == null) {
            throw new IllegalStateException();
        }
    }
}
