package ru.rsreu.shop.impl;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.Order;
import ru.rsreu.shop.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DisruptorShop extends Shop {

    private static final int RING_BUFFER_SIZE = 2048;

    private final Lock availableItemsLock = new ReentrantLock();
    private final Lock purchasedItemsStorageLock = new ReentrantLock();

    private final Map<Item, Long> availableItems = new HashMap<>();
    private final Map<Client, Map<Item, Long>> purchasedItemsStorage = new HashMap<>();
    private final Disruptor<Order> disruptor;

    private final RingBuffer<Order> ringBuffer;

    public DisruptorShop(Long money) {
        super(money);
        disruptor = new Disruptor<>(
                Order::new,
                RING_BUFFER_SIZE,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new BusySpinWaitStrategy()
        );
        ringBuffer = disruptor.getRingBuffer();
//        disruptor.handleEventsWith((ringBuffer1, sequences) -> );
    }

    @Override
    public void addItem(Item item, Long number) {

    }

    @Override
    public boolean buyItem(Item item, Long numberItems, Client client) {
        return false;
    }

    @Override
    public Map<Item, Long> getAvailableItems() {
        return null;
    }

    @Override
    public Map<Client, Map<Item, Long>> getPurchasedItems() {
        return null;
    }

    private void parseOrder(Item item, Long numberItems, Client client) {
        if (!availableItems.containsKey(item) ||
                availableItems.get(item) < numberItems ||
                client.getMoney() < item.getPrice() * numberItems ||
                numberItems < 1) {
            return;
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
    }
}
