package ru.rsreu.factory;

import ru.rsreu.shop.Shop;
import ru.rsreu.shop.impl.DisruptorShop;
import ru.rsreu.shop.impl.QueueShop;

import java.util.Random;

import static ru.rsreu.utils.TestSettings.*;
import static ru.rsreu.utils.TestSettings.MIN_NUMBER_ITEM;

public class TestDisruptorShopFactory implements ShopFactory {
    private Random random = new Random();

    @Override
    public Shop getShop() {
        TestItemFactory itemFactory = TestItemFactory.INSTANCE;
        int numberDifferentItems = random.nextInt(
                MAX_NUMBER_DIFFERENT_ITEM_IN_SHOP - MIN_NUMBER_DIFFERENT_ITEM_IN_SHOP
        ) + MIN_NUMBER_DIFFERENT_ITEM_IN_SHOP;

        Shop shop = new DisruptorShop(SHOP_MONEY);
        for (int i = 0; i < numberDifferentItems; i++) {
            long numberItem = random.nextInt(
                    MAX_NUMBER_ITEM - MIN_NUMBER_ITEM
            ) + MIN_NUMBER_ITEM;
            shop.addItem(itemFactory.getItem(), numberItem);
        }
        return shop;
    }
}
