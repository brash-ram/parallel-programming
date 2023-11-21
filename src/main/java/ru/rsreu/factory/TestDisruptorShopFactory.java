package ru.rsreu.factory;

import ru.rsreu.shop.ItemShop;
import ru.rsreu.shop.impl.DisruptorItemShop;

import java.util.Random;

import static ru.rsreu.utils.TestSettings.*;
import static ru.rsreu.utils.TestSettings.MIN_NUMBER_ITEM;

public class TestDisruptorShopFactory implements ShopFactory {
    private Random random = new Random();

    @Override
    public ItemShop getShop() {
        TestItemFactory itemFactory = TestItemFactory.INSTANCE;
        int numberDifferentItems = random.nextInt(
                MAX_NUMBER_DIFFERENT_ITEM_IN_SHOP - MIN_NUMBER_DIFFERENT_ITEM_IN_SHOP
        ) + MIN_NUMBER_DIFFERENT_ITEM_IN_SHOP;

        ItemShop itemShop = new DisruptorItemShop(SHOP_MONEY);
        for (int i = 0; i < numberDifferentItems; i++) {
            long numberItem = random.nextInt(
                    MAX_NUMBER_ITEM - MIN_NUMBER_ITEM
            ) + MIN_NUMBER_ITEM;
            itemShop.addItem(itemFactory.getItem(), numberItem);
        }
        return itemShop;
    }
}
