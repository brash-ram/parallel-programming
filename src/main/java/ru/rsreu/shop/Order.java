package ru.rsreu.shop;

import com.lmax.disruptor.EventFactory;
import lombok.Getter;
import lombok.Setter;
import ru.rsreu.client.Client;

@Getter
@Setter
public class Order {

    private Client client;
    private Long numberItems;
    private Item item;
}
