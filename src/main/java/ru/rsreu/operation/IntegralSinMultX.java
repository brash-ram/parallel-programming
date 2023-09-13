package ru.rsreu.operation;

import java.util.function.Function;

public class IntegralSinMultX {
    private double accuracy;

    public IntegralSinMultX(double accuracy) {
        this.accuracy = accuracy;
    }

    public double calculateIntegral() {
        double a = 0;
        double b = 1;

        return calculateIntegral(a, b, x -> Math.sin(x) * x);
    }

    public long getExecutionTimeMillis() {
        long start = System.currentTimeMillis();
        calculateIntegral();
        return System.currentTimeMillis() - start;
    }

    public double getExecutionTimeSec() {
        long millis = getExecutionTimeMillis();
        return millis / 1000.0;
    }

    private double calculateIntegral(double a, double b, Function<Double, Double> f) {
        double n = 1;

        double integralPrev;
        double integral = 0;

        do {
            integralPrev = integral;

            double h = (b - a) / n;
            double sum = 0;

            for (int i = 0; i < n; i++) {
                double x0 = a + i * h;
                double x1 = a + (i + 1) * h;
                sum += (f.apply(x0) + f.apply(x1));
            }

            integral = (h / 2) * sum;
            n *= 2;
        } while (Math.abs(integral - integralPrev) > accuracy);

        return integral;
    }

}