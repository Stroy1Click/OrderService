package ru.stroy1click.order.service;

import ru.stroy1click.order.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto get(Long id);

    List<OrderDto> getByUserId(Long userId);

    OrderDto create(OrderDto orderDto);

    void update(Long id, OrderDto orderDto);

    void delete(Long id);
}
