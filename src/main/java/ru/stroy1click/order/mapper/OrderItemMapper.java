package ru.stroy1click.order.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.entity.OrderItem;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemMapper implements Mappable<OrderItem, OrderItemDto>{

    private final ModelMapper modelMapper;

    @Override
    public OrderItem toEntity(OrderItemDto orderDto) {
        return this.modelMapper.map(orderDto, OrderItem.class);
    }

    @Override
    public OrderItemDto toDto(OrderItem order) {
        return this.modelMapper.map(order, OrderItemDto.class);
    }

    @Override
    public List<OrderItemDto> toDto(List<OrderItem> e) {
        return e.stream()
                .map(orderItem -> this.modelMapper.map(orderItem, OrderItemDto.class))
                .toList();
    }

    public List<OrderItem> toEntity(List<OrderItemDto> e) {
        return e.stream()
                .map(orderItemDto -> this.modelMapper.map(orderItemDto, OrderItem.class))
                .toList();
    }

}
