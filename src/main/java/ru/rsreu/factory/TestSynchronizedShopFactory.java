package ru.rsreu.factory;

import ru.rsreu.shop.ItemShop;
import ru.rsreu.shop.impl.SynchronizedItemShop;

import java.util.Random;

import static ru.rsreu.utils.TestSettings.*;

public class TestSynchronizedShopFactory implements ShopFactory {

    private Random random = new Random();

    @Override
    public ItemShop getShop() {
        TestItemFactory itemFactory = TestItemFactory.INSTANCE;

        ItemShop itemShop = new SynchronizedItemShop(SHOP_MONEY);
        for (int i = 0; i < MAX_NUMBER_DIFFERENT_ITEM_IN_SHOP; i++) {
            itemShop.addItem(itemFactory.getItem(), (long)MAX_NUMBER_ITEM);
        }
        return itemShop;
    }
}
