package ru.rsreu.operation;

import ru.rsreu.ProgressBar;
import ru.rsreu.Storage;
import ru.rsreu.ThreadPool;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class IntegralSinMultX {
    private double accuracy;

    private static final int NUMBER_THREADS = 5;
    private static final int NUMBER_AVAILABLE_THREADS = 5;

    private ThreadPool threadPool;

    public IntegralSinMultX(double accuracy) {
        this.accuracy = accuracy;
        threadPool = new ThreadPool(NUMBER_THREADS, NUMBER_AVAILABLE_THREADS);
        // accuracy = 1e-14 == 2 sec
    }

    public double calculateIntegral() throws InterruptedException {
        double a = 0;
        double b = 1;

        return calculateIntegral(a, b, x -> Math.sin(x) * x);
    }

    private double calculateIntegral(double a, double b, Function<Double, Double> f) throws InterruptedException {
        long n = 1L;

        double integralPrev = 0;
        double integral = 0;

        CountDownLatch countDownLatch;

        ProgressBar.getInstance().setH0(accuracy);

        System.out.println("PROGRESS [0%]");

        for (int i = 0; i < NUMBER_THREADS; i++) {
            countDownLatch = new CountDownLatch(1);
            threadPool.runTask(getRunCalcIntegral(a, b, n, f, countDownLatch), countDownLatch);
            n *= 2;
        }

        do {
            Integer progress = ProgressBar.getInstance().getUpdatedProgress();
            if (progress != null) {
                System.out.println("PROGRESS [" + progress + "%]");
            }

            integralPrev = integral;
            integral = Storage.get();

            if (Math.abs(integral - integralPrev) > accuracy) {
                countDownLatch = new CountDownLatch(1);
                threadPool.runTask(getRunCalcIntegral(a, b, n, f, countDownLatch), countDownLatch);
            }
            n *= 2;

        } while (Math.abs(integral - integralPrev) > accuracy);

        System.out.println("PROGRESS [100%]");

        threadPool.stopAll();

        return integral;
    }

    private Runnable getRunCalcIntegral(double a, double b, long n, Function<Double, Double> f, CountDownLatch countDownLatch) {
        return () -> {
            double h = (b - a) / n;
            double sum = 0;

            for (int i = 0; i < n; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                double x0 = a + i * h;
                double x1 = a + (i + 1) * h;
                sum += (f.apply(x0) + f.apply(x1));
            }

            Storage.add((h / 2) * sum);
            countDownLatch.countDown();
        };
    }
}