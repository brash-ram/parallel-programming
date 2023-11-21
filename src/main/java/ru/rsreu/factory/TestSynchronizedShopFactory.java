package ru.rsreu.factory;

import ru.rsreu.shop.Shop;
import ru.rsreu.shop.impl.QueueShop;
import ru.rsreu.shop.impl.SynchronizedShop;

import java.util.Random;

import static ru.rsreu.utils.TestSettings.*;

public class TestSynchronizedShopFactory implements ShopFactory {

    private Random random = new Random();

    @Override
    public Shop getShop() {
        TestItemFactory itemFactory = TestItemFactory.INSTANCE;

        Shop shop = new SynchronizedShop(SHOP_MONEY);
        for (int i = 0; i < MAX_NUMBER_DIFFERENT_ITEM_IN_SHOP; i++) {
            shop.addItem(itemFactory.getItem(), (long)MAX_NUMBER_ITEM);
        }
        return shop;
    }
}
