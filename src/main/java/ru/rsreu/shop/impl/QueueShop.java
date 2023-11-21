package ru.rsreu.shop.impl;

import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.Order;
import ru.rsreu.shop.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class QueueShop extends Shop implements AutoCloseable {
    private final Map<Item, Long> availableItems = new HashMap<>();
    private final Map<Client, Map<Item, Long>> purchasedItemsStorage = new HashMap<>();

    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(10000);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public QueueShop(long money) {
        super(money);
        executorService.execute(this::ordersProcessing);
    }

    @Override
    public void addItem(Item item, long number) {
        items.add(item);
        availableItems.put(item, number);
    }

    @Override
    public CompletableFuture<Boolean> buyItem(Item item, long numberItems, Client client) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (item == null || client == null || client.getMoney() < item.getPrice() * numberItems || numberItems < 1) {
            result.complete(false);
            return result;
        }

        Order order = new Order(client, item, numberItems, result);
        boolean pushedQueue = orderQueue.offer(order);
        if (!pushedQueue) {
            result.complete(false);
        }
        return result;
    }

    @Override
    public Map<Item, Long> getAvailableItems() {
        synchronized (orderQueue) {
            while (orderQueue.size() > 0) {
                try {
                    orderQueue.wait();
                } catch (InterruptedException ignored) {
                    return null;
                }
            }
        }
        return availableItems;
    }

    @Override
    public Map<Client, Map<Item, Long>> getPurchasedItems() {
        synchronized (orderQueue) {
            while (orderQueue.size() > 0) {
                try {
                    orderQueue.wait();
                } catch (InterruptedException ignored) {
                    return null;
                }
            }
        }
        return purchasedItemsStorage;
    }

    private void ordersProcessing() {
        while (true) {
            Order order = null;
            try {
                order = orderQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (order == null) continue;

            if (order.getResult().isDone()) continue;

            parseOrder(order.getItem(), order.getNumberItems(), order.getClient(), order.getResult());

            if (orderQueue.size() == 0) {
                synchronized (orderQueue) {
                    orderQueue.notifyAll();
                }
            }

        }
    }

    private void parseOrder(Item item, long numberItems, Client client, CompletableFuture<Boolean> result) {
        if (!availableItems.containsKey(item) ||
                availableItems.get(item) < numberItems) {
            return;
        }

        boolean isAvailableItem = false;
        if (availableItems.containsKey(item) && availableItems.get(item) >= numberItems) {
            if (availableItems.get(item) > numberItems) {
                availableItems.put(item, availableItems.get(item) - numberItems);
            } else {
                availableItems.remove(item);
            }
            isAvailableItem = true;
        }

        if (isAvailableItem) {
            if (purchasedItemsStorage.containsKey(client)) {
                Map<Item, Long> purchasedItems = purchasedItemsStorage.get(client);
                if (purchasedItems.containsKey(item)) {
                    purchasedItems.put(item, purchasedItems.get(item) + numberItems);
                } else {
                    purchasedItems.put(item, numberItems);
                }
            } else {
                Map<Item, Long> purchasedItems = new HashMap<>();
                purchasedItems.put(item, numberItems);
                purchasedItemsStorage.put(client, purchasedItems);
            }
            this.money += item.getPrice() * numberItems;
            client.spendMoney(item.getPrice() * numberItems);
            result.complete(true);
        }
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
