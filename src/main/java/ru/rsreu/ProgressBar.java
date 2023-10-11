package ru.rsreu;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProgressBar {

    private static ProgressBar instance;

    private static final Lock LOCK = new ReentrantLock();

    private double value;
    private boolean isEdited = false;

    private static final int STEP_INFO = 20;
    private static final int PERCENT = 100 / STEP_INFO;

    private int hx = 1;
    private Double h0;
    private double h;

    public static ProgressBar getInstance() {
        if (instance == null) {
            LOCK.lock();
            instance = new ProgressBar();
            LOCK.unlock();
        }
        return instance;
    }

    public void setH0(Double accuracy) {
        this.h0 = Math.pow(accuracy, 1.0 / STEP_INFO);
        this.h = h0;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setValueAndUpdateProgress(double value) {
        LOCK.lock();
        double valuePrev = this.value;
        this.value = value;
        if (h * h0 > Math.abs(value - valuePrev)) {
            h *= h0;
            hx++;
            isEdited = true;
        }
        LOCK.unlock();
    }

    public Integer getUpdatedProgress() {
        LOCK.lock();
        if (isEdited) {
            int progress = hx * PERCENT;
            h *= h0;
            hx++;
            isEdited = false;
            LOCK.unlock();
            return progress;
        } else {
            LOCK.unlock();
            return null;
        }

    }
}
