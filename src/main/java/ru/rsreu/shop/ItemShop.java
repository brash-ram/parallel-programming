package ru.rsreu.shop;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemShop implements Shop {

    protected Long money;

    protected final List<Item> items = new ArrayList<>();

    public ItemShop(Long money) {
        this.money = money;
    }

    public Long getMoney() {
        return money;
    }

    public List<Item> getItems() {
        return items;
    }

    public abstract void addItem(Item item, long number);
}
