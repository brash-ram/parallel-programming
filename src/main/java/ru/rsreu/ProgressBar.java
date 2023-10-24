package ru.rsreu;


import ru.rsreu.synch.Lock;

public class ProgressBar {

    private static ProgressBar instance;

    private static final Lock LOCK = new Lock();

    private double value;
    private boolean isEdited = false;

    private static final int STEP_INFO = 20;
    private static final int PERCENT = 100 / STEP_INFO;

    private int hx = 1;
    private Double h0;
    private double h;

    public static ProgressBar getInstance() throws InterruptedException {
        if (instance == null) {
            LOCK.lock();
            try {
                instance = new ProgressBar();
            } finally {
                LOCK.unlock();
            }
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
        try {
            LOCK.lock();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            double valuePrev = this.value;
            this.value = value;
            if (h * h0 > Math.abs(value - valuePrev)) {
                h *= h0;
                hx++;
                isEdited = true;
            }
        } finally {
            LOCK.unlock();
        }
    }

    public Integer getUpdatedProgress() throws InterruptedException {
        LOCK.lock();
        try {
            if (isEdited) {
                int progress = hx * PERCENT;
                h *= h0;
                hx++;
                isEdited = false;
                return progress;
            } else {
                return null;
            }
        } finally {
            LOCK.unlock();
        }

    }
}
