package ru.rsreu;

import ru.rsreu.operation.IntegralSinMultX;

public class IntegralMain {

    public static void main(String[] args) {
        try {
            System.out.println(new IntegralSinMultX(1e-14).calculateIntegral());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
