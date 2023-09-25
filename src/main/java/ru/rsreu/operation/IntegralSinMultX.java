package ru.rsreu.operation;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class IntegralSinMultX implements Runnable {
    private double accuracy;
    private static final int STEP_INFO = 20;

    private ExecutorService executorService;

    public IntegralSinMultX(double accuracy) {
        this.accuracy = accuracy;
        executorService = Executors.newFixedThreadPool(2);
        // accuracy = 1e-14 == 2 sec
    }

    @Override
    public void run() {
        try {
            System.out.println("Значение интеграла = " + calculateIntegral());
        } catch (Exception ex) {
            System.out.println("Вычисление интеграла прервано");
        }

    }

    public double calculateIntegral() throws Exception {
        double a = 0;
        double b = 1;

        return calculateIntegral(a, b, x -> Math.sin(x) * x);
    }

    public long getExecutionTimeMillis() {
        long start = System.currentTimeMillis();
        try {
            calculateIntegral();
        } catch (Exception ex) {
            System.out.println("Вычисление интеграла прервано");
        }
        return System.currentTimeMillis() - start;
    }

    public double getExecutionTimeSec() {
        long millis = getExecutionTimeMillis();
        return millis / 1000.0;
    }

    private double calculateIntegral(double a, double b, Function<Double, Double> f) throws Exception {
        long n = 1L;

        double integralPrev = 0;
        double integral = 0;

        int hx = 1;
        double h0Accuracy = Math.pow(accuracy, 1.0 / STEP_INFO);
        double hAccuracy = h0Accuracy;

        int percent = 100 / STEP_INFO;

        Future<Double> futureIntegral1 = calcIntegralInFuture(a, b, n, f);
        n *= 2;
        Future<Double> futureIntegral2 = calcIntegralInFuture(a, b, n, f);
        n *= 2;

        System.out.println("0%");

        do {
            if (Thread.currentThread().isInterrupted()) {
                throw new Exception();
            }

            if (hAccuracy > Math.abs(integral - integralPrev)) {
                while (hAccuracy * h0Accuracy > Math.abs(integral - integralPrev) && hx != 1) {
                    hAccuracy *= h0Accuracy;
                    hx++;
                }
                System.out.println(hx * percent + "%");
                hAccuracy *= h0Accuracy;
                hx++;
            }

            integralPrev = integral;

            while (!futureIntegral1.isDone()) {
            }

            integral = futureIntegral1.get();
            futureIntegral1 = futureIntegral2;

            futureIntegral2 = calcIntegralInFuture(a, b, n, f);
            n *= 2;

        } while (Math.abs(integral - integralPrev) > accuracy);

        System.out.println("100%");

        return integral;
    }

    private Future<Double> calcIntegralInFuture(double a, double b, long n, Function<Double, Double> f) {
        return executorService.submit(() -> {
            double h = (b - a) / n;
            double sum = 0;

            for (int i = 0; i < n; i++) {
                double x0 = a + i * h;
                double x1 = a + (i + 1) * h;
                sum += (f.apply(x0) + f.apply(x1));
            }

            return (h / 2) * sum;
        });
    }
}