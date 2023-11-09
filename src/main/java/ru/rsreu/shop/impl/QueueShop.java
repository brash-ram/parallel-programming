package ru.rsreu.shop.impl;

import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.Shop;
import ru.rsreu.utils.TestSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueShop extends Shop {

    private static final int SIZE_SHOP_QUEUE = 1000;
    private final Lock availableItemsLock = new ReentrantLock();
    private final Lock purchasedItemsStorageLock = new ReentrantLock();

    private final Map<Item, Long> availableItems = new HashMap<>();
    private final Map<Client, Map<Item, Long>> purchasedItemsStorage = new HashMap<>();

    private final int NUMBER_THREADS = 3;
    private final BlockingQueue<Map<Client, Map<Item, Long>>> orderQueue =
            new LinkedBlockingQueue<>();


    public QueueShop(long money) {
        super(money);
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
        for (int i = 0; i < NUMBER_THREADS; i++) {
            executorService.execute(this::ordersProcessing);
        }
    }

    @Override
    public void addItem(Item item, Long number) {
        items.add(item);
        availableItems.put(item, number);
    }

    @Override
    public boolean buyItem(Item item, Long numberItems, Client client) {
        if (!availableItems.containsKey(item) ||
                availableItems.get(item) < numberItems ||
                client.getMoney() < item.getPrice() * numberItems ||
                numberItems < 1) {
            return false;
        }

        Map<Client, Map<Item, Long>> order = new HashMap<>();
        Map<Item, Long> orderItem = new HashMap<>();
        orderItem.put(item, numberItems);
        order.put(client, orderItem);
        return orderQueue.offer(order);
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
        return purchasedItemsStorage;
    }

    private void ordersProcessing() {
        while (true) {
            Map<Client, Map<Item, Long>> order = null;
            try {
                order = orderQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            if (order == null) continue;

            for (Map.Entry<Client, Map<Item, Long>> entry : order.entrySet()) {
                for (Map.Entry<Item, Long> itemEntry : entry.getValue().entrySet()) {
                    Client client = entry.getKey();
                    Item item = itemEntry.getKey();
                    Long numberItems = itemEntry.getValue();
                    parseOrder(item, numberItems, client);
                }
            }
            if (orderQueue.size() == 0) {
                synchronized (orderQueue) {
                    orderQueue.notify();
                }
            }

        }
    }

    private void parseOrder(Item item, Long numberItems, Client client) {
        availableItemsLock.lock();
        boolean isAvailableItem = false;
        try {
            if (availableItems.containsKey(item) && availableItems.get(item) >= numberItems) {
                if (availableItems.get(item) > numberItems) {
                    availableItems.put(item, availableItems.get(item) - numberItems);
                } else {
                    availableItems.remove(item);
                }
                isAvailableItem = true;
            }
        } finally {
            availableItemsLock.unlock();
        }

        if (isAvailableItem) {
            purchasedItemsStorageLock.lock();
            try {
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
            } finally {
                purchasedItemsStorageLock.unlock();
            }
        }

        return;
    }
}
