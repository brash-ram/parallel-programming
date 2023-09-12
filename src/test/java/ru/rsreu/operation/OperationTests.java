package ru.rsreu.operation;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperationTests {

    @RepeatedTest(50)
    public void countSpecialSymbolsInSimpleStringTest() {
        String testString = "3425'345]gdfs[*";
        int numberSymbols = 4;
        assertEquals(numberSymbols, TestOperation.countSpecialSymbolsInString(testString));
    }

    @RepeatedTest(50)
    public void countSpecialSymbolsInEmptyStringTest() {
        String testString = "";
        int numberSymbols = 0;
        assertEquals(numberSymbols, TestOperation.countSpecialSymbolsInString(testString));
    }

    @RepeatedTest(50)
    public void countSpecialSymbolsInNumberStringTest() {
        String testString = "345324532463425";
        int numberSymbols = 0;
        assertEquals(numberSymbols, TestOperation.countSpecialSymbolsInString(testString));
    }

    @RepeatedTest(50)
    public void countSpecialSymbolsInSpaceStringTest() {
        String testString = "    ";
        int numberSymbols = 0;
        assertEquals(numberSymbols, TestOperation.countSpecialSymbolsInString(testString));
    }
}
