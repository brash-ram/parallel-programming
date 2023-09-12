package ru.rsreu.operation;

public class TestOperation {

    public static int countSpecialSymbolsInString(String line) {
        return line.replaceAll("[A-Za-z0-9\\s]", "").length();
    }
}
