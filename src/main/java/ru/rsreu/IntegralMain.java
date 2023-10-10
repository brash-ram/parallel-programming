package ru.rsreu;

import ru.rsreu.operation.IntegralSinMultX;

public class IntegralMain {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            System.out.println(new IntegralSinMultX(1e-14).calculateIntegral());
            System.out.println((System.currentTimeMillis() - start) / 1000.0);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
