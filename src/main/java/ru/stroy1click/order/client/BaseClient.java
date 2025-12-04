package ru.stroy1click.order.client;

public interface BaseClient<ID, T>{

    T get(ID id);
}
