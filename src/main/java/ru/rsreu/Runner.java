package ru.rsreu;

import ru.rsreu.task.TaskLevel;
import ru.rsreu.task.TaskManager;

import java.util.Scanner;

public class Runner {

    public void menuLoop() {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = TaskManager.getInstance();
        printMenu();
        while (true) {
//            taskManager.clearPool();
            String input = scanner.nextLine();
            String[] command = input.split(" ");

            if (command.length > 2) continue;

            switch (command[0]) {
                case "start":
                    if (command.length > 1 && TaskLevel.availableLevel.containsKey(command[1])) {
                        long id = taskManager.startTask(TaskLevel.availableLevel.get(command[1]));
                        System.out.println("Запущена задача " + id);
                    }
                    break;

                case "stop":
                    if (command.length > 1) {
                        boolean status = taskManager.stopTask(command[1]);
                        if (status) {
                            System.out.println("Задача завершена");
                        } else {
                            System.out.println("Такой задачи не существует");
                        }
                    }
                    break;

                case "await":
                    if (command.length > 1) {
                        boolean status = false;
                        try {
                            status = taskManager.awaitTask(command[1]);
                        } catch (InterruptedException ex) {
                            System.out.println("Ожидание задачи завершилось ошибкой " + ex.getMessage());
                        }

                        if (status) {
                            System.out.println("Ожидание задачи завершилось");
                        } else {
                            System.out.println("Такой задачи не существует");
                        }
                    }
                    break;

                case "exit":
                    return;

                default:
                    continue;
            }
            printMenu();
        }
    }

    private void printMenu() {
        String sb = "\nstart <n> - запускает задачу с параметром времени n\n" +
                "n = (1 - быстро выполнимо; 2 - выполнимо; 3 - невыполнимо)\n" +
                "stop <n> - останавливает задачу с номером n\n" +
                "await <n> - консоль блокируется до момента завершения задачи с номером n\n" +
                "exit - завершает выполнения всей программы";
        System.out.println(sb);
    }
}
