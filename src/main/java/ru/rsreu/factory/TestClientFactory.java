package ru.rsreu.factory;

import ru.rsreu.client.Client;
import ru.rsreu.utils.TestSettings;

import java.util.Random;

public class TestClientFactory {

    public static final TestClientFactory INSTANCE = new TestClientFactory();

    private long idSequence = 1L;

    private Random random = new Random();

    public TestClientFactory() {
    }

    public Client getClient() {
        return new Client().setId(idSequence++).setMoney((long)random.nextInt(TestSettings.MAX_CLIENT_MONEY));
    }
}
