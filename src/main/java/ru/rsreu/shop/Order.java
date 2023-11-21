package ru.rsreu.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.rsreu.client.Client;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
@Setter
public class Order {
    private Client client;
    private Item item;
    private long numberItems;
    private CompletableFuture<Boolean> result;
}
