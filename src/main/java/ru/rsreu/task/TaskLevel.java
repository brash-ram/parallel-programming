package ru.rsreu.task;

import java.util.*;

public enum TaskLevel {
    LOW(1e-13),
    MEDIUM(1e-14),
    HARD(1e-15);

    private double level;
//    public static final List<String> availableLevel = Arrays.asList("1", "2", "3");
    public static final Map<String, TaskLevel> availableLevel = new HashMap<String, TaskLevel>() {{
            put("1", LOW);
            put("2", MEDIUM);
            put("3", HARD);
    }};

    TaskLevel(double level) {
        this.level = level;
    }

    public double getLevel() {
        return level;
    }
}
