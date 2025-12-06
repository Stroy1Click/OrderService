package ru.stroy1click.order.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import ru.stroy1click.order.cache.CacheClear;
import ru.stroy1click.order.client.ProductClient;
import ru.stroy1click.order.client.UserClient;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.dto.ProductDto;
import ru.stroy1click.order.entity.Order;
import ru.stroy1click.order.entity.OrderItem;
import ru.stroy1click.order.exception.NotFoundException;
import ru.stroy1click.order.mapper.OrderItemMapper;
import ru.stroy1click.order.mapper.OrderMapper;
import ru.stroy1click.order.model.OrderStatus;
import ru.stroy1click.order.repository.OrderRepository;
import ru.stroy1click.order.service.impl.OrderServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CacheClear cacheClear;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    private Long orderId;
    private Long userId;
    private Order order;
    private OrderDto orderDto;
    private OrderItem orderItem;
    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация общих данных для всех тестов
        this.orderId = 1L;
        this.userId = 10L;

        this.orderItemDto = OrderItemDto.builder()
                .productId(100)
                .build();

        this.orderDto = OrderDto.builder()
                .id(this.orderId)
                .orderStatus(OrderStatus.CREATED)
                .notes("Old notes")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .contactPhone("+79999999999")
                .userId(this.userId)
                .orderItems(List.of(this.orderItemDto))
                .build();

        this.orderItem = new OrderItem();
        this.orderItem.setId(500L);

        this.order = new Order();
        this.order.setId(this.orderId);
        this.order.setUserId(this.userId);
        this.order.setOrderItems(List.of(this.orderItem));
    }


    @Test
    void get_ShouldReturnOrderDto_WhenOrderExists() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.of(this.order));
        when(this.orderMapper.toDto(this.order)).thenReturn(this.orderDto);

        OrderDto result = this.orderService.get(this.orderId);

        assertNotNull(result);
        assertEquals(this.orderDto, result);
        verify(this.orderRepository).findById(this.orderId);
        verify(this.orderMapper).toDto(this.order);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenOrderDoesNotExist() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(any(), any(), any())).thenReturn("Заказ не найден");

        assertThrows(NotFoundException.class, () -> this.orderService.get(this.orderId));
        verify(this.orderRepository).findById(this.orderId);
    }


    @Test
    void getByUserId_ShouldReturnList_WhenExists() {
        List<Order> orders = List.of(this.order);
        List<OrderDto> dtoList = List.of(this.orderDto);

        when(this.orderRepository.findByUserId(this.userId)).thenReturn(orders);
        when(this.orderMapper.toDto(orders)).thenReturn(dtoList);

        List<OrderDto> result = this.orderService.getByUserId(this.userId);

        assertEquals(1, result.size());
        assertEquals(this.orderDto, result.getFirst());
        verify(this.orderRepository).findByUserId(this.userId);
    }


    @Test
    void create_ShouldSaveOrder_WhenInputValid() {
        Order newOrderEntity = new Order();

        when(this.orderMapper.toEntity(this.orderDto)).thenReturn(newOrderEntity);
        when(this.orderItemMapper.toEntity(anyList())).thenReturn(List.of(this.orderItem));


        when(this.productClient.get(100)).thenReturn(new ProductDto());

        this.orderService.create(this.orderDto);

        verify(this.userClient).get(this.userId);
        verify(this.productClient).get(100);
        assertEquals(newOrderEntity, this.orderItem.getOrder());
        verify(this.orderRepository).save(newOrderEntity);
    }


    @Test
    void update_ShouldSaveUpdatedOrder_WhenExists() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.of(this.order));
        when(this.orderMapper.toEntity(any(OrderDto.class))).thenReturn(this.order);

        this.orderService.update(this.orderId, this.orderDto);

        ArgumentCaptor<OrderDto> dtoCaptor = ArgumentCaptor.forClass(OrderDto.class);
        verify(this.orderMapper).toEntity(dtoCaptor.capture());

        OrderDto capturedDto = dtoCaptor.getValue();

        assertEquals(this.orderId, capturedDto.getId());
        assertTrue(capturedDto.getUpdatedAt().isAfter(this.orderDto.getUpdatedAt()));

        verify(this.orderRepository).save(this.order);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenOrderDoesNotExist() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(any(), any(), any())).thenReturn("Заказ не найден");

        assertThrows(NotFoundException.class, () -> this.orderService.update(this.orderId, this.orderDto));
        verify(this.orderRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteOrder_WhenExists() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.of(this.order));

        this.orderService.delete(this.orderId);

        verify(this.orderRepository).delete(this.order);
        verify(this.cacheClear).clearOrdersByUserId(this.userId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotFound() {
        when(this.orderRepository.findById(this.orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(any(), any(), any())).thenReturn("Заказ не найден");

        assertThrows(NotFoundException.class, () -> this.orderService.delete(this.orderId));
        verify(this.cacheClear, never()).clearOrdersByUserId(any());
    }
}
