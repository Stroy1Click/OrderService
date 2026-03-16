package ru.stroy1click.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.order.cache.CacheClear;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.entity.Order;
import ru.stroy1click.order.entity.OrderItem;
import ru.stroy1click.order.mapper.OrderItemMapper;
import ru.stroy1click.order.mapper.OrderMapper;
import ru.stroy1click.order.repository.OrderRepository;
import ru.stroy1click.order.service.OrderService;
import ru.stroy1click.outbox.service.OutboxEventService;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    private final MessageSource messageSource;

    private final CacheClear cacheClear;

    private final OutboxEventService outboxEventService;

    private final static String ORDER_CREATED_TOPIC = "order-created-events";

    @Override
    @Cacheable(cacheNames = "order", key = "#id")
    public OrderDto get(Long id) {
        log.info("get {}", id);

        return this.orderMapper.toDto(this.orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.order.not_found",
                                null,
                                Locale.getDefault()
                        )
                )));
    }

    @Override
    @Cacheable(value = "allOrders")
    public List<OrderDto> getAll() {
        log.info("getAll");

        return this.orderMapper.toDto(
                this.orderRepository.findAll()
        );
    }

    @Override
    @Cacheable(cacheNames = "ordersByUserId", key = "#userId")
    public List<OrderDto> getByUserId(Long userId) {
        log.info("getByUserId {}", userId);

        return this.orderMapper.toDto(
                this.orderRepository.findByUserId(userId)
        );
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "ordersByUserId", key = "#orderDto.userId"),
            @CacheEvict(value = "allOrders", allEntries = true)
    })
    public OrderDto create(OrderDto orderDto) {
        log.info("create {}", orderDto);

        orderDto.setId(null);
        Order order = this.orderMapper.toEntity(orderDto);

        // явно проставляем order всем каскадам
        List<OrderItem> orderItems = this.orderItemMapper.toEntity(orderDto.getOrderItems())
                .stream()
                .peek(orderItem -> orderItem.setOrder(order))
                .toList();
        order.setOrderItems(orderItems);

        OrderDto createdOrder = this.orderMapper.toDto(
                this.orderRepository.save(order)
        );

        this.outboxEventService.save(ORDER_CREATED_TOPIC, createdEvent(createdOrder));

        return createdOrder;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "order", key = "#id"),
            @CacheEvict(value = "ordersByUserId", key = "#orderDto.userId"),
            @CacheEvict(value = "allOrders", allEntries = true)
    })
    public void update(Long id, OrderDto orderDto) {
        log.info("update {}, {}", id, orderDto);

        this.orderRepository.findById(id).ifPresentOrElse(order -> {
            order.setLegalForm(orderDto.getLegalForm());
            order.setLegalName(orderDto.getLegalName());
            order.setInn(orderDto.getInn());
            order.setKpp(orderDto.getKpp());
            order.setNotes(orderDto.getNotes());
            order.setContactName(orderDto.getContactName());
            order.setContactPhone(orderDto.getContactPhone());
            order.setContactEmail(orderDto.getContactEmail());
            order.setDeliveryAddress(orderDto.getDeliveryAddress());
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
    @Caching(evict = {
            @CacheEvict(cacheNames = "order", key = "#id"),
            @CacheEvict(value = "allOrders", allEntries = true)
    })
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

    private OrderCreatedEvent createdEvent(OrderDto createdOrder){
        return OrderCreatedEvent.builder()
                .id(createdOrder.getId())
                .contactEmail(createdOrder.getContactEmail())
                .contactName(createdOrder.getContactName())
                .contactPhone(createdOrder.getContactPhone())
                .createdAt(createdOrder.getCreatedAt())
                .updatedAt(createdOrder.getUpdatedAt())
                .deliveryAddress(createdOrder.getDeliveryAddress())
                .inn(createdOrder.getInn())
                .kpp(createdOrder.getKpp())
                .notes(createdOrder.getNotes())
                .legalForm(createdOrder.getLegalForm())
                .legalName(createdOrder.getLegalName())
                .orderItems(this.orderItemMapper.toEvent(createdOrder.getOrderItems()))
                .orderStatus(createdOrder.getOrderStatus())
                .userId(createdOrder.getUserId())
                .build();
    }
}
