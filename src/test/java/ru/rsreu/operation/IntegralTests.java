package ru.rsreu.operation;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntegralTests {

    @Test()
    public void timeTest() {
        IntegralSinMultX integral = new IntegralSinMultX(1e-14);
        double sec = integral.getExecutionTimeSec();
        System.out.println(sec);
        assertTrue(sec > 1);
    }

//    @Test()
//    public void timeLongTest() {
//        IntegralSinMultX integral = new IntegralSinMultX(0.000000000000008);
//        double sec = integral.getExecutionTimeSec();
//        System.out.println(sec);
//        assertTrue(sec > 1);
//    }


    @RepeatedTest(10)
    public void timeRepeatedTest() {
        IntegralSinMultX integral = new IntegralSinMultX(1e-14);
        double sec = integral.getExecutionTimeSec();
        System.out.println(sec);
        assertTrue(sec > 1);
    }
}
