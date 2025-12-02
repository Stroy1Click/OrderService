package ru.stroy1click.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.stroy1click.order.cache.CacheClear;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.entity.Order;
import ru.stroy1click.order.exception.NotFoundException;
import ru.stroy1click.order.mapper.OrderMapper;
import ru.stroy1click.order.repository.OrderRepository;
import ru.stroy1click.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper mapper;

    private final MessageSource messageSource;

    private final CacheClear cacheClear;

    @Override
    @Cacheable(cacheNames = "order", key = "#id")
    public OrderDto get(Long id) {
        log.info("get {}", id);
        return this.mapper.toDto(this.orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        this.messageSource.getMessage(
                                "",
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
    @CacheEvict(cacheNames = "ordersByUserId", key = "#orderDto.userId")
    public void create(OrderDto orderDto) {
        log.info("create {}", orderDto);
        this.orderRepository.save(
                this.mapper.toEntity(orderDto)
        );
    }

    @Override
    @CacheEvict(cacheNames = "order", key = "#id")
    public void update(Long id, OrderDto orderDto) {
        log.info("update {}, {}", id, orderDto);
        this.orderRepository.findById(id).ifPresentOrElse(order -> {
            OrderDto updatedOrderDto = OrderDto.builder()
                    .id(id)
                    .notes(orderDto.getNotes())
                    .quantity(orderDto.getQuantity())
                    .orderStatus(orderDto.getOrderStatus())
                    .createdAt(orderDto.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .productId(orderDto.getProductId())
                    .contactPhone(orderDto.getContactPhone())
                    .userId(orderDto.getUserId())
                    .build();

            this.orderRepository.save(this.mapper.toEntity(updatedOrderDto));
        }, () -> {
            throw new NotFoundException(
                    this.messageSource.getMessage(
                            "error.category.not_found",
                            null,
                            Locale.getDefault()
                    )
            );
        });
    }

    @Override
    @CacheEvict(cacheNames = "order", key = "#id")
    public void delete(Long id) {
        log.info("delete {}", id);
        Order order = this.orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.category.not_found",
                                null,
                                Locale.getDefault()
                        )
                )
        );

        this.orderRepository.delete(order);

        this.cacheClear.clearOrdersByUserId(id);
    }
}
