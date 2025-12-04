package ru.stroy1click.order.client;

import ru.stroy1click.order.dto.OrderDto;

public interface NotificationClient {

    void sendOrderNotification(OrderDto orderDto);
}
