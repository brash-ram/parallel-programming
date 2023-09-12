package ru.rsreu;

import ru.rsreu.operation.IntegralSinMultX;

public class IntegralApplication {
    public static void main(String[] args) {
        double accuracy = 1e-14;
        Thread thread = new Thread(new IntegralSinMultX(accuracy));
        thread.start();
    }
}
