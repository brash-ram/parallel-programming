package ru.rsreu.task;

import ru.rsreu.operation.IntegralSinMultX;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private static TaskManager instance;
    private Long ID_SEQUENCE = 1L;
    private Map<String, Thread> pool = new HashMap<>();

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public long startTask(TaskLevel level) {
        Thread thread = new Thread(new IntegralSinMultX(level.getLevel()));
        thread.setName("THREAD_" + ID_SEQUENCE);
        pool.put(ID_SEQUENCE.toString(), thread);
        thread.start();
        return ID_SEQUENCE++;
    }

    public boolean stopTask(String id) {
        if (pool.containsKey(id)) {
            pool.get(id).interrupt();
            pool.remove(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean awaitTask(String id) throws InterruptedException {
        if (pool.containsKey(id)) {
            System.out.println("Поток блокируется до завершения задачи " + id);
            pool.get(id).join();
            pool.remove(id);
            return true;
        } else {
            return false;
        }
    }
}
