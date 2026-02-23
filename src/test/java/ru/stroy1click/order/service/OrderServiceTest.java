package ru.stroy1click.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import ru.stroy1click.common.dto.LegalForm;
import ru.stroy1click.common.dto.OrderStatus;
import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.order.cache.CacheClear;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.entity.Order;
import ru.stroy1click.order.entity.OrderItem;
import ru.stroy1click.order.mapper.OrderItemMapper;
import ru.stroy1click.order.mapper.OrderMapper;
import ru.stroy1click.order.repository.OrderRepository;
import ru.stroy1click.order.service.impl.OrderServiceImpl;
import ru.stroy1click.outbox.service.OutboxEventService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CacheClear cacheClear;

    @Mock
    private OutboxEventService outboxEventService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private final static String ORDER_CREATED_TOPIC = "order-created-events";

    private Long orderId;

    private Long userId;

    private Order order;

    private OrderDto orderDto;

    private OrderItem orderItem;

    private OrderItemDto orderItemDto;

    @BeforeEach
    public void setUp() {
        orderId = 1L;
        userId = 10L;

        orderItemDto = OrderItemDto.builder()
                .productId(100)
                .build();

        orderDto = OrderDto.builder()
                .id(orderId)
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Old notes")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(userId)
                .orderItems(List.of(orderItemDto))
                .build();

        orderItem = new OrderItem();
        orderItem.setId(500L);

        order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setOrderItems(List.of(orderItem));
    }


    @Test
    public void get_WhenOrderExists_ShouldReturnOrderDto() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(this.orderMapper.toDto(order)).thenReturn(orderDto);

        //Act
        OrderDto result = this.orderService.get(orderId);

        //Assert
        assertNotNull(result);
        assertEquals(orderDto, result);
        verify(this.orderRepository).findById(orderId);
        verify(this.orderMapper).toDto(order);
    }

    @Test
    public void get_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(eq("error.order.not_found"), any(), any())).thenReturn("Заказ не найден");

        //Act & Assert
        assertThrows(NotFoundException.class, () -> this.orderService.get(orderId));
        verify(this.orderRepository).findById(orderId);
    }

    @Test
    public void getAll_WhenOrdersExist_ShouldReturnList(){
        //Arrange
        when(this.orderRepository.findAll()).thenReturn(List.of(order));
        when(this.orderMapper.toDto(List.of(order))).thenReturn(List.of(orderDto));

        //Act
        List<OrderDto> dtoList = this.orderService.getAll();

        //Assert
        assertEquals(1, dtoList.size());
        verify(this.orderRepository).findAll();
        verify(this.orderMapper).toDto(List.of(order));
    }


    @Test
    public void getByUserId_WhenOrderExists_ShouldReturnList() {
        //Arrange
        List<Order> orders = List.of(order);
        List<OrderDto> dtoList = List.of(orderDto);
        when(this.orderRepository.findByUserId(userId)).thenReturn(orders);
        when(this.orderMapper.toDto(orders)).thenReturn(dtoList);

        //Act
        List<OrderDto> result = this.orderService.getByUserId(userId);

        //Assert
        assertEquals(1, result.size());
        assertEquals(this.orderDto, result.getFirst());
        verify(this.orderRepository).findByUserId(userId);
    }


    @Test
    public void create_WhenValidDataProvided_ShouldSaveAndReturnCreatedOrderAndSaveOutboxEvent() {
        //Arrange
        OrderDto dto = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Old notes")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(userId)
                .orderItems(List.of(orderItemDto))
                .build();
        when(this.orderMapper.toEntity(dto)).thenReturn(order);
        when(this.orderItemMapper.toEntity(anyList())).thenReturn(List.of(orderItem));
        doNothing().when(this.outboxEventService).save(eq(ORDER_CREATED_TOPIC),
                any(OrderCreatedEvent.class));
        when(this.orderRepository.save(any(Order.class))).thenReturn(order);
        when(this.orderMapper.toDto(order)).thenReturn(orderDto);

        //Act
        OrderDto createdOrder = this.orderService.create(dto);

        //Assert
        verify(this.orderRepository).save(order);
        verify(this.outboxEventService).save(eq(ORDER_CREATED_TOPIC), any(OrderCreatedEvent.class));
        assertNotNull(createdOrder.getId());
        assertEquals(orderId, createdOrder.getId());
    }


    @Test
    public void update_WhenValidDataProvidedAndOrderExists_ShouldSaveUpdatedOrder() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(this.orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);

        //Act
        this.orderService.update(orderId, orderDto);

        //Assert
        ArgumentCaptor<OrderDto> dtoCaptor = ArgumentCaptor.forClass(OrderDto.class);
        verify(this.orderMapper).toEntity(dtoCaptor.capture());

        OrderDto capturedDto = dtoCaptor.getValue();

        assertEquals(orderId, capturedDto.getId());
        verify(this.orderRepository).save(order);
    }

    @Test
    public void update_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(eq("error.order.not_found"), any(), any())).thenReturn("Заказ не найден");

        //Act & Assert
        assertThrows(NotFoundException.class, () -> this.orderService.update(orderId, orderDto));
        verify(this.orderRepository, never()).save(any());
    }

    @Test
    public void delete_WhenOrderExists_ShouldDeleteOrder() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //Act
        this.orderService.delete(orderId);

        //Assert
        verify(this.orderRepository).delete(order);
        verify(this.cacheClear).clearOrdersByUserId(userId);
    }

    @Test
    public void delete_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(this.messageSource.getMessage(eq("error.order.not_found"), any(), any())).thenReturn("Заказ не найден");

        //Act & Assert
        assertThrows(NotFoundException.class, () -> this.orderService.delete(orderId));
        verify(this.cacheClear, never()).clearOrdersByUserId(any());
    }
}
