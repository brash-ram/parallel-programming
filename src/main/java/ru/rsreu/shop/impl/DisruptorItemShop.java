package ru.rsreu.shop.impl;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import ru.rsreu.client.Client;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.ItemShop;
import ru.rsreu.shop.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class DisruptorItemShop extends ItemShop implements AutoCloseable {

    private static final int RING_BUFFER_SIZE = 2048;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Map<Item, Long> availableItems = new HashMap<>();
    private final Map<Client, Map<Item, Long>> purchasedItemsStorage = new HashMap<>();
    private Disruptor<Order> disruptor;
    private RingBuffer<Order> ringBuffer;

    public DisruptorItemShop(Long money) {
        super(money);
        CountDownLatch latch = new CountDownLatch(1);
        executorService.execute(() -> {
            disruptor = new Disruptor<>(
                    Order::new,
                    RING_BUFFER_SIZE,
                    DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI,
                    new BlockingWaitStrategy()
            );
            disruptor.handleEventsWith((order, l, b) ->
                    parseOrder(order)
            );
            ringBuffer = disruptor.start();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
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
        long sequenceId = ringBuffer.next();
        Order valueEvent = ringBuffer.get(sequenceId);
        valueEvent.setItem(item);
        valueEvent.setNumberItems(numberItems);
        valueEvent.setClient(client);
        valueEvent.setResult(result);
        ringBuffer.publish(sequenceId);
        return result;
    }

    @Override
    public Map<Item, Long> getAvailableItems() {
//        try {
//            disruptor.shutdown(10, TimeUnit.SECONDS);
//        } catch (TimeoutException ex) {
//            ex.printStackTrace();
//            return null;
//        }
        return availableItems;
    }

    @Override
    public Map<Client, Map<Item, Long>> getPurchasedItems() {
        return purchasedItemsStorage;
    }

    private void parseOrder(Order order) {
        Item item = order.getItem();
        Client client = order.getClient();
        long numberItems = order.getNumberItems();
        CompletableFuture<Boolean> result = order.getResult();

        if (!availableItems.containsKey(item) || availableItems.get(item) < numberItems) {
            result.complete(false);
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
        } else {
            result.complete(false);
        }
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
