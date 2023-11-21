package ru.rsreu.shop;

import ru.rsreu.client.Client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Shop {

    CompletableFuture<Boolean> buyItem(Item item, long numberItems, Client client);
    Map<Item, Long> getAvailableItems();

    Map<Client, Map<Item, Long>> getPurchasedItems();
}
