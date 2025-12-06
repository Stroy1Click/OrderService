package ru.stroy1click.order.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.stroy1click.order.client.ProductClient;
import ru.stroy1click.order.client.UserClient;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.dto.ProductDto;
import ru.stroy1click.order.dto.UserDto;
import ru.stroy1click.order.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;


@Import({TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ProductClient productClient;

    @Test
    public void getOrder_ShouldReturnOrderAndItems() {
        Long id = 1L;

        ResponseEntity<OrderDto> response = this.testRestTemplate.getForEntity(
                "/api/v1/orders/" + id,
                OrderDto.class,
                id
        );

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(id, response.getBody().getId());
        Assertions.assertEquals(OrderStatus.CREATED, response.getBody().getOrderStatus());
        Assertions.assertEquals(2, response.getBody().getOrderItems().size());
    }

    @Test
    public void createOrder_ShouldReturnSuccessfulMessage() {
        when(this.userClient.get(600L)).thenReturn(new UserDto());
        when(this.productClient.get(33)).thenReturn(new ProductDto());

        OrderItemDto item1 = OrderItemDto.builder().productId(33).quantity(3).build();

        OrderDto dto = OrderDto.builder()
                .id(null)
                .notes("Новый заказ для тестирования POST")
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of(item1))
                .contactPhone("+79001112233")
                .userId(600L)
                .build();

        ResponseEntity<String> response = this.testRestTemplate.
                postForEntity("/api/v1/orders", dto, String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Заказ создан", response.getBody());
    }

    @Test
    public void updateOrder_ShouldReturnOk_AndConfirmChange() {
        Long orderIdToUpdate = 2L;

        OrderDto updateDto = OrderDto.builder()
                .id(orderIdToUpdate)
                .notes("Заказ обновлен: статус изменен")
                .orderStatus(OrderStatus.SHIPPED)
                .createdAt(LocalDateTime.of(2024,9,15,12,0,0))
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of())
                .contactPhone("89241111111")
                .userId(1L)
                .build();

        ResponseEntity<String> response = this.testRestTemplate.exchange(
                        "/api/v1/orders/" + "/{id}",
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateDto),
                        String.class,
                        orderIdToUpdate
                );

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Заказ обновлён", response.getBody());

        ResponseEntity<OrderDto> finalGetResponse = this.testRestTemplate.getForEntity(
                "/api/v1/orders/" + "/{id}",
                OrderDto.class,
                orderIdToUpdate
        );
        Assertions.assertEquals(OrderStatus.SHIPPED, finalGetResponse.getBody().getOrderStatus());
        Assertions.assertEquals("Заказ обновлен: статус изменен", finalGetResponse.getBody().getNotes());
    }

    @Test
    public void deleteOrder_ShouldReturnOk_AndConfirmDeletion() {
        Long orderIdToDelete = 3L;

        ResponseEntity<String> response = this.testRestTemplate
                .exchange(
                         "/api/v1/orders/" + "{id}",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        String.class,
                        orderIdToDelete
                );

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Заказ удалён", response.getBody());

        // Проверка, что заказ больше не найден
        ResponseEntity<ProblemDetail> getResponse = this.testRestTemplate
                .getForEntity("/api/v1/orders/" + "/{id}", ProblemDetail.class, orderIdToDelete);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }


    @Test
    public void getOrder_NotFound_ShouldReturnProblemDetail() {
        Long nonExistentId = 99999L;

        ResponseEntity<ProblemDetail> response = this.testRestTemplate
                .getForEntity("/api/v1/orders/" + "{id}", ProblemDetail.class, nonExistentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Assertions.assertEquals("Не найдено", response.getBody().getTitle());
        Assertions.assertEquals("Пользователь с электронной почтой Заказ не найден не найден", response.getBody().getDetail());
    }

    @Test
    public void createOrder_ValidationFailure_InvalidContactPhonePattern() {
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).build();

        OrderDto invalidPhoneOrder = OrderDto.builder()
                .id(1L)
                .notes("Тест плохого телефона")
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of(item))
                .contactPhone("1234567") // Невалидный формат
                .userId(600L)
                .build();

        ResponseEntity<ProblemDetail> response = this.testRestTemplate.postForEntity(
                "/api/v1/orders",
                invalidPhoneOrder,
                ProblemDetail.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Ошибка валидации", response.getBody().getTitle());
        Assertions.assertEquals("Номер телефона должен быть валидным", response.getBody().getDetail());
    }

    @Test
    public void createOrder_ValidationFailure_NonPositiveUserId() {
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).build();

        OrderDto invalidUserOrder = OrderDto.builder()
                .id(1L)
                .notes("Тест плохого User ID")
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of(item))
                .contactPhone("+79001112233")
                .userId(0L) // Нарушает @Positive
                .build();

        ResponseEntity<ProblemDetail> response = this.testRestTemplate.postForEntity(
                "/api/v1/orders",
                invalidUserOrder,
                ProblemDetail.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Ошибка валидации", response.getBody().getTitle());
        Assertions.assertEquals("Id пользователя не может быть меньше 1", response.getBody().getDetail());
    }

    @Test
    public void createOrder_ValidationFailure_OrderItemQuantityIsZero() {
        OrderItemDto invalidItem = OrderItemDto.builder()
                .productId(10)
                .quantity(0) // Нарушает @Min(1)
                .build();

        OrderDto invalidQuantityOrder = OrderDto.builder()
                .id(1L)
                .notes("Тест невалидного количество товаров")
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of(invalidItem))
                .contactPhone("89241111111") // Невалидный формат
                .userId(600L)
                .build();

        ResponseEntity<ProblemDetail> response = this.testRestTemplate.postForEntity(
                "/api/v1/orders",
                invalidQuantityOrder,
                ProblemDetail.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Ошибка валидации", response.getBody().getTitle());
        Assertions.assertEquals("Количество единиц товара не может быть меньше 1", response.getBody().getDetail());
    }

    @Test
    public void updateOrder_NotFound_ShouldReturnProblemDetail() {
        Long nonExistentId = 9999L;
        OrderItemDto item = OrderItemDto.builder()
                .productId(10)
                .quantity(1)
                .build();

        OrderDto updatedDto = OrderDto.builder()
                .id(1L)
                .notes("Тест невалидного количество товаров")
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(List.of(item))
                .contactPhone("89241111111")
                .userId(600L)
                .build();

        ResponseEntity<ProblemDetail> response = this.testRestTemplate.exchange(
                "/api/v1/orders/" + nonExistentId,
                HttpMethod.PATCH,
                new HttpEntity<>(updatedDto),
                ProblemDetail.class,
                nonExistentId
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Не найдено", response.getBody().getTitle());
    }
}
