package ru.rsreu.shop;

import ru.rsreu.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Shop {

    protected Long money;

    protected final List<Item> items = new ArrayList<>();

    public Shop(Long money) {
        this.money = money;
    }

    public Long getMoney() {
        return money;
    }

    public List<Item> getItems() {
        return items;
    }

    public abstract void addItem(Item item, Long number);

    public abstract boolean buyItem(Item item, Long numberItems, Client client);


    public abstract Map<Item, Long> getAvailableItems();

    public abstract Map<Client, Map<Item, Long>> getPurchasedItems();
}
