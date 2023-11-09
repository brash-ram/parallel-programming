package ru.rsreu.shop.impl;

import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedShop extends Shop {

    private final Lock availableItemsLock = new ReentrantLock();
    private final Lock purchasedItemsStorageLock = new ReentrantLock();

    private final Map<Item, Long> availableItems = new HashMap<>();
    private final Map<Client, Map<Item, Long>> purchasedItemsStorage = new HashMap<>();


    public SynchronizedShop(long money) {
        super(money);
    }

    @Override
    public void addItem(Item item, Long number) {
        items.add(item);
        availableItemsLock.lock();
        try {
            availableItems.put(item, number);
        } finally {
            availableItemsLock.unlock();
        }
    }

    @Override
    public boolean buyItem(Item item, Long numberItems, Client client) {

//        if (!availableItems.containsKey(item)) {
//            System.out.println("Товар " + item.getId() + " отсутствует");
//        }
//        if (availableItems.containsKey(item) ||
//                availableItems.get(item) < numberItems) {
//            System.out.println("Товара " + item.getId() + " недостаточно");
//        }
//        if (client.getMoney() < item.getPrice() * numberItems) {
//            System.out.println("У клиента " + client.getId() + " недостаточно денег");
//        }


        if (!availableItems.containsKey(item) ||
                availableItems.get(item) < numberItems ||
                client.getMoney() < item.getPrice() * numberItems ||
                numberItems < 1) {
            return false;
        }

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

        return isAvailableItem;
    }

    @Override
    public Map<Item, Long> getAvailableItems() {
        return availableItems;
    }

    @Override
    public Map<Client, Map<Item, Long>> getPurchasedItems() {
        return purchasedItemsStorage;
    }
}
