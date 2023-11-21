package ru.rsreu.unit;

import org.junit.jupiter.api.Test;
import ru.rsreu.client.Client;
import ru.rsreu.factory.TestClientFactory;
import ru.rsreu.factory.TestItemFactory;
import ru.rsreu.factory.ShopFactory;
import ru.rsreu.factory.TestSynchronizedShopFactory;
import ru.rsreu.shop.Item;
import ru.rsreu.shop.ItemShop;

import java.util.Map;

import static ru.rsreu.utils.TestSettings.*;
import static org.junit.jupiter.api.Assertions.*;

public class FactoryTests {

    @Test
    public void itemFactoryTest() {
        TestItemFactory itemFactory = new TestItemFactory();

        Item item1 = itemFactory.getItem();
        assertEquals(1L, item1.getId());
        assertTrue(item1.getPrice() > MIN_ITEM_PRICE && item1.getPrice() < MAX_ITEM_PRICE);

        Item item2 = itemFactory.getItem();
        assertEquals(2L, item2.getId());
        assertTrue(item2.getPrice() > MIN_ITEM_PRICE && item2.getPrice() < MAX_ITEM_PRICE);
    }

    @Test
    public void clientFactoryTest() {
        TestClientFactory testClientFactory = new TestClientFactory();

        Client client1 = testClientFactory.getClient();
        assertEquals(1L, client1.getId());
        assertTrue(client1.getMoney() > 0 && client1.getMoney() < MAX_CLIENT_MONEY);

        Client client2 = testClientFactory.getClient();
        assertEquals(2L, client2.getId());
        assertTrue(client2.getMoney() > 0 && client2.getMoney() < MAX_CLIENT_MONEY);
    }

    @Test
    public void shopFactoryTest() {
        ShopFactory shopFactory = new TestSynchronizedShopFactory();
        ItemShop itemShop = shopFactory.getShop();
        Map<Item, Long> mapItems = itemShop.getAvailableItems();

        assertTrue(mapItems.keySet().size() > MIN_NUMBER_DIFFERENT_ITEM_IN_SHOP &&
                mapItems.keySet().size() < MAX_NUMBER_DIFFERENT_ITEM_IN_SHOP);
        Map<Client, Map<Item, Long>> purchasedItems = itemShop.getPurchasedItems();
        assertEquals(0, purchasedItems.keySet().size());
    }
}
