package ru.rsreu;

import ru.rsreu.operation.IntegralSinMultX;

public class IntegralMain {
//    public static void main(String[] args) {
//        double accuracy = 1e-14;
//        Thread thread = new Thread(new IntegralSinMultX(accuracy));
//        thread.start();
//    }

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.menuLoop();
    }
}
