package ru.rsreu.shop.impl;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.Order;
import ru.rsreu.shop.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DisruptorShop extends Shop {

    private static final int RING_BUFFER_SIZE = 2048;
    private final int NUMBER_THREADS = 3;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    private final Lock availableItemsLock = new ReentrantLock();
    private final Lock purchasedItemsStorageLock = new ReentrantLock();
    private final Object lockItems = new Object();

    private boolean endOfBatchRing = false;

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
                new BlockingWaitStrategy()
        );
        ringBuffer = disruptor.getRingBuffer();
        disruptor.handleEventsWith((order, l, b) ->
                executorService.execute(() -> parseOrder(order.getItem(), order.getNumberItems(), order.getClient(), b))
        );
//        disruptor.handleEventsWith(
//                (order, l, b) -> parseOrder(order.getItem(), order.getNumberItems(), order.getClient(), b)
//        );
//        CountDownLatch startDisruptor = new CountDownLatch(1);
//        executorService.execute(() -> {
//            disruptor.handleEventsWith(
//                    (order, l, b) -> parseOrder(order.getItem(), order.getNumberItems(), order.getClient(), b)
//            );
//            disruptor.start();
//            startDisruptor.countDown();
//        });
//        try {
//            startDisruptor.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        disruptor.start();

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
        long sequenceId = ringBuffer.next();
        Order valueEvent = ringBuffer.get(sequenceId);
        valueEvent.setItem(item);
        valueEvent.setNumberItems(numberItems);
        valueEvent.setClient(client);
        ringBuffer.publish(sequenceId);
        return true;
    }

    @Override
    public Map<Item, Long> getAvailableItems() {
//        synchronized (lockItems) {
//            while (endOfBatchRing) {
//                try {
//                    lockItems.wait();
//                } catch (InterruptedException ignored) {
//                    return null;
//                }
//            }
//        }
//        try {
//            disruptor.shutdown(100, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return availableItems;
    }

    @Override
    public Map<Client, Map<Item, Long>> getPurchasedItems() {
        return purchasedItemsStorage;
    }

    private void parseOrder(Item item, Long numberItems, Client client, boolean endOfBatch) {
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

//        if (endOfBatch) {
//            synchronized (lockItems) {
//                endOfBatchRing = true;
//                lockItems.notify();
//            }
//        }
    }
}
