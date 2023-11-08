package ru.rsreu.factory;

import ru.rsreu.shop.Item;

import java.util.Random;

import static ru.rsreu.utils.TestSettings.*;

public class TestItemFactory {

    public static final TestItemFactory INSTANCE = new TestItemFactory();

    private long idSequence = 1L;

    private Random random = new Random();

    public TestItemFactory() {
    }

    public Item getItem() {
        return new Item().setId(idSequence++).setPrice(
                (long)random.nextInt(
                        MAX_ITEM_PRICE - MIN_ITEM_PRICE
                ) + MIN_ITEM_PRICE
        );
    }
}
