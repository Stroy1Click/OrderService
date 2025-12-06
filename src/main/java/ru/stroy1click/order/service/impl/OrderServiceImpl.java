package ru.stroy1click.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.order.cache.CacheClear;
import ru.stroy1click.order.client.ProductClient;
import ru.stroy1click.order.client.UserClient;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.entity.Order;
import ru.stroy1click.order.entity.OrderItem;
import ru.stroy1click.order.exception.NotFoundException;
import ru.stroy1click.order.mapper.OrderItemMapper;
import ru.stroy1click.order.mapper.OrderMapper;
import ru.stroy1click.order.repository.OrderRepository;
import ru.stroy1click.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper mapper;

    private final OrderItemMapper orderItemMapper;

    private final MessageSource messageSource;

    private final CacheClear cacheClear;

    private final UserClient userClient;

    private final ProductClient productClient;

    @Override
    @Cacheable(cacheNames = "order", key = "#id")
    public OrderDto get(Long id) {
        log.info("get {}", id);
        return this.mapper.toDto(this.orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.order.not_found",
                                null,
                                Locale.getDefault()
                        )
                )));
    }

    @Override
    @Cacheable(cacheNames = "ordersByUserId", key = "#userId")
    public List<OrderDto> getByUserId(Long userId) {
        log.info("getByUserId {}", userId);
        return this.mapper.toDto(
                this.orderRepository.findByUserId(userId)
        );
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "ordersByUserId", key = "#orderDto.userId")
    public void create(OrderDto orderDto) {
        log.info("create {}", orderDto);

        this.userClient.get(orderDto.getUserId());
        orderDto.getOrderItems().stream()
                .map(orderItemDto -> this.productClient.get(orderItemDto.getProductId()))
                .toList();

        orderDto.setId(null);
        Order order = this.mapper.toEntity(orderDto);

        List<OrderItem> orderItems = this.orderItemMapper.toEntity(orderDto.getOrderItems())
                .stream()
                .peek(i -> i.setOrder(order))
                .toList();

        order.setOrderItems(orderItems);

        this.orderRepository.save(order);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "order", key = "#id")
    public void update(Long id, OrderDto orderDto) {
        log.info("update {}, {}", id, orderDto);
        this.orderRepository.findById(id).ifPresentOrElse(order -> {
            List<OrderItemDto> orderItems = this.orderItemMapper.toDto(order.getOrderItems());
            OrderDto updatedOrderDto = OrderDto.builder()
                    .id(id)
                    .notes(orderDto.getNotes())
                    .orderStatus(orderDto.getOrderStatus())
                    .createdAt(orderDto.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .orderItems(orderItems)
                    .contactPhone(orderDto.getContactPhone())
                    .userId(order.getUserId())
                    .build();

            this.orderRepository.save(this.mapper.toEntity(updatedOrderDto));
        }, () -> {
            throw new NotFoundException(
                    this.messageSource.getMessage(
                            "error.order.not_found",
                            null,
                            Locale.getDefault()
                    )
            );
        });
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "order", key = "#id")
    public void delete(Long id) {
        log.info("delete {}", id);
        Order order = this.orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.order.not_found",
                                null,
                                Locale.getDefault()
                        )
                )
        );

        this.orderRepository.delete(order);

        this.cacheClear.clearOrdersByUserId(order.getUserId());
    }
}
