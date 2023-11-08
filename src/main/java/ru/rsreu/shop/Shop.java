package ru.rsreu.shop;

import ru.rsreu.client.Client;

import java.util.List;
import java.util.Map;

public interface Shop {

    Long getMoney();

    void addItem(Item item, Long number);

    boolean buyItem(Item item, Long numberItems, Client client);

    List<Item> getItems();

    Map<Item, Long> getAvailableItems();

    Map<Client, Map<Item, Long>> getPurchasedItems();
}
