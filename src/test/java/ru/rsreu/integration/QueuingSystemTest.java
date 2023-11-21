package ru.rsreu.integration;

import org.junit.jupiter.api.RepeatedTest;
import ru.rsreu.client.Client;
import ru.rsreu.factory.*;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.ItemShop;
import ru.rsreu.utils.TestSettings;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueuingSystemTest {

    private final int NUMBER_THREADS = 50;

    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    @RepeatedTest(5)
    public void disruptorShopTest() throws InterruptedException {
        ShopFactory shopFactory = new TestDisruptorShopFactory();
        long start = System.currentTimeMillis();
        queuingSystemTest(shopFactory);
        System.out.println("Time: " + (System.currentTimeMillis() - start));
        executorService.shutdownNow();
    }

    @RepeatedTest(5)
    public void queueShopTest() throws InterruptedException {
        ShopFactory shopFactory = new TestQueueShopFactory();
        long start = System.currentTimeMillis();
        queuingSystemTest(shopFactory);
        System.out.println("Time: " + (System.currentTimeMillis() - start));
        executorService.shutdownNow();
    }

    @RepeatedTest(5)
    public void synchronizedShopTest() throws InterruptedException {
        ShopFactory shopFactory = new TestSynchronizedShopFactory();
        long start = System.currentTimeMillis();
        queuingSystemTest(shopFactory);
        System.out.println("Time: " + (System.currentTimeMillis() - start));
        executorService.shutdownNow();
    }

    public void queuingSystemTest(ShopFactory shopFactory) throws InterruptedException {
        ItemShop itemShop = shopFactory.getShop();
        List<Client> clients = getTestClients();

        long startClientsMoney = clients.stream().map(Client::getMoney).reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException("Нет денег у клиентов"));
        Map<Item, Long> startShopItems = new HashMap<>(itemShop.getAvailableItems());
        long startShopMoney = itemShop.getMoney();

        openShop(itemShop, clients);

        Map<Item, Long> endShopItems = new HashMap<>(itemShop.getAvailableItems());
        for (Map.Entry<Client, Map<Item, Long>> entry : itemShop.getPurchasedItems().entrySet()) {
            for (Map.Entry<Item, Long> itemEntry : entry.getValue().entrySet()) {
                if (endShopItems.containsKey(itemEntry.getKey())) {
                    endShopItems.put(itemEntry.getKey(), endShopItems.get(itemEntry.getKey()) + itemEntry.getValue());
                } else {
                    endShopItems.put(itemEntry.getKey(), itemEntry.getValue());
                }
            }
        }
        assertEquals(startShopItems, endShopItems);

        long endClientsMoney = clients.stream().map(Client::getMoney).reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException("Нет денег у клиентов"));
        long spentClientsMoney = clients.stream().map(Client::getSpentMoney).reduce(Long::sum)
                .orElseThrow(() -> new RuntimeException("Нет денег у клиентов"));
        long endShopMoney = itemShop.getMoney();

        assertEquals(startClientsMoney, spentClientsMoney + endClientsMoney);
        assertEquals(startClientsMoney + startShopMoney, endClientsMoney + endShopMoney);
    }

    public void openShop(ItemShop itemShop, List<Client> clients) throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(NUMBER_THREADS);
        CountDownLatch endLatch = new CountDownLatch(NUMBER_THREADS);
        for (int i = 0; i < NUMBER_THREADS; i++) {
            Client client = clients.get(i);

            executorService.execute(() -> {
                startLatch.countDown();
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                try {
                    for (int j = 0; j < TestSettings.NUMBER_CLIENT_OPERATIONS; j++) {
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }
                        try {
                            buyItem(itemShop, client);
                        } catch (InterruptedException | ExecutionException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(Thread.currentThread().getName() + " --- " + ex.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        endLatch.await();
    }

    private void buyItem(ItemShop itemShop, Client client) throws ExecutionException, InterruptedException {
        Random random = new Random();

        int randomItemIndex = random.nextInt(itemShop.getItems().size());
        long numberItems = random.nextInt(TestSettings.MAX_NUMBER_ITEM / 1000) + 1;

        Item purchasedItem = itemShop.getItems().get(randomItemIndex);

        CompletableFuture<Boolean> isPurchased = itemShop.buyItem(purchasedItem, numberItems, client);
        isPurchased.get();

//        String purchased = "";
//        String label = "[✓] ";
//        if (!isPurchased.get()) {
//            purchased = " не";
//            label = "[❌] ";
//        }
//
//        System.out.println(
//                label + "Клиент " + client.getId() + purchased + " купил " +
//                        numberItems + " ед. товара " + purchasedItem.getId()
//        );
    }

    private List<Client> getTestClients() {
        TestClientFactory testClientFactory = TestClientFactory.INSTANCE;
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < NUMBER_THREADS; i++) {
            clients.add(testClientFactory.getClient());
        }
        return clients;
    }
}
