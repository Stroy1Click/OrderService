package ru.stroy1click.order.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.entity.Order;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper implements Mappable<Order, OrderDto>{

    private final ModelMapper modelMapper;

    @Override
    public Order toEntity(OrderDto orderDto) {
        return this.modelMapper.map(orderDto, Order.class);
    }

    @Override
    public OrderDto toDto(Order order) {
        return this.modelMapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> toDto(List<Order> e) {
        return e.stream()
                .map(product -> this.modelMapper.map(product, OrderDto.class))
                .toList();
    }

}
