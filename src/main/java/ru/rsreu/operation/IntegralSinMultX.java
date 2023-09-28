package ru.rsreu.operation;

import java.util.concurrent.*;
import java.util.function.Function;

public class IntegralSinMultX implements Runnable {
    private double accuracy;
    private static final int STEP_INFO = 20;

    private ExecutorService executorService;

    public IntegralSinMultX(double accuracy) {
        this.accuracy = accuracy;
        executorService = Executors.newCachedThreadPool();
        // accuracy = 1e-14 == 2 sec
    }

    @Override
    public void run() {
        try {
            System.out.println("Значение интеграла = " + calculateIntegral());
        } catch (InterruptedException ex) {
            System.out.println("Вычисление интеграла прервано");
        } catch (Exception ex) {
            System.out.println("Вычисление интеграла прервано " + ex.getMessage());
        }

    }

    public double calculateIntegral() throws InterruptedException, ExecutionException {
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

    private double calculateIntegral(double a, double b, Function<Double, Double> f) throws InterruptedException, ExecutionException {
        long n = 1L;

        double integralPrev = 0;
        double integral = 0;

        int hx = 1;
        double h0Accuracy = Math.pow(accuracy, 1.0 / STEP_INFO);
        double hAccuracy = h0Accuracy;

        int percent = 100 / STEP_INFO;

        Future<Double> futureIntegral1 = null;
        Future<Double> futureIntegral2 = null;

        System.out.println("0%");

        do {

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

            if (hx * percent <= 40) {
                integral = calcIntegral(a, b, n, f);
                n *= 2;
            } else {
                if (futureIntegral1 == null) {
                    futureIntegral1 = calcIntegralInFuture(a, b, n, f);
                    n *= 2;
                }
                if (futureIntegral2 == null) {
                    futureIntegral2 = calcIntegralInFuture(a, b, n, f);
                    n *= 2;
                }

                integral = futureIntegral1.get();
                futureIntegral1 = futureIntegral2;

                futureIntegral2 = calcIntegralInFuture(a, b, n, f);
                n *= 2;
            }

        } while (Math.abs(integral - integralPrev) > accuracy);

        System.out.println("100%");

        return integral;
    }

    private Future<Double> calcIntegralInFuture(double a, double b, long n, Function<Double, Double> f) {
        return executorService.submit(() -> calcIntegral(a, b, n, f));
    }

    private Double calcIntegral(double a, double b, long n, Function<Double, Double> f) {
        double h = (b - a) / n;
        double sum = 0;

        for (int i = 0; i < n; i++) {
            double x0 = a + i * h;
            double x1 = a + (i + 1) * h;
            sum += (f.apply(x0) + f.apply(x1));
        }

        return (h / 2) * sum;
    }
}