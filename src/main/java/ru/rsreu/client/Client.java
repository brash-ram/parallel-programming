package ru.rsreu.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class Client implements Comparable<Client> {
    private Long id;
    private Long money;
    private Long spentMoney = 0L;

    public void spendMoney(Long money) {
        this.money -= money;
        spentMoney += money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Client o) {
        return Long.compare(id, o.id);
    }
}
