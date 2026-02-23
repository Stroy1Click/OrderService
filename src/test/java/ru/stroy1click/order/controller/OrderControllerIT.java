package ru.stroy1click.order.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import ru.stroy1click.common.dto.LegalForm;
import ru.stroy1click.common.dto.OrderStatus;
import ru.stroy1click.common.dto.Unit;
import ru.stroy1click.order.config.TestcontainersConfiguration;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Order(1)
    public void get_WhenOrderExists_ShouldReturnOrder() {
        //Arrange
        Long id = 1L;

        //Act
        ResponseEntity<OrderDto> response = this.testRestTemplate.getForEntity(
                "/api/v1/orders/" + id,
                OrderDto.class,
                id
        );

        //Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(id, response.getBody().getId());
        assertEquals(2, response.getBody().getOrderItems().size());
    }

    @Test
    @Order(2)
    public void getAll_WhenOrdersExist_ShouldReturnAllOrders() {
        //Act
        ResponseEntity<OrderDto[]> response = this.testRestTemplate.getForEntity(
                "/api/v1/orders",
                OrderDto[].class
        );

        //Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length != 0);
    }

    @Test
    @Order(3)
    public void create_WhenValidDataProvided_ShouldReturnSuccessfulMessage() {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(33).quantity(3).unit(Unit.KG).price(BigDecimal.ONE).productTitle("Product Title")
                .productTitle("Title").build();
        OrderDto dto = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();

        //Act
        ResponseEntity<OrderDto> response = this.testRestTemplate.
                postForEntity("/api/v1/orders", dto, OrderDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Новый заказ для тестирования", response.getBody().getNotes());
    }

    @Test
    @Order(4)
    public void update_WhenValidDataProvidedAndOrderExists_ShouldReturnOk() {
        //Arrange
        Long orderIdToUpdate = 2L;
        OrderItemDto item = OrderItemDto.builder().id(3L).productId(33).quantity(3).unit(Unit.KG).price(BigDecimal.ONE).productTitle("Product Title")
                .productTitle("Title").build();
        OrderDto orderDto = OrderDto.builder()
                .id(orderIdToUpdate)
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.SHIPPED)
                .notes("Заказ обновлен: статус изменен")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of())
                .build();

        //Act
        ResponseEntity<String> response = this.testRestTemplate.exchange(
                        "/api/v1/orders/" + orderIdToUpdate,
                        HttpMethod.PATCH,
                        new HttpEntity<>(orderDto),
                        String.class,
                        orderIdToUpdate
                );

        //Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Заказ обновлён", response.getBody());

        ResponseEntity<OrderDto> updatedOrder = this.testRestTemplate.getForEntity(
                "/api/v1/orders/" + orderIdToUpdate,
                OrderDto.class,
                orderIdToUpdate
        );
        assertEquals(OrderStatus.SHIPPED, updatedOrder.getBody().getOrderStatus());
        assertEquals("Заказ обновлен: статус изменен", updatedOrder.getBody().getNotes());
    }

    @Test
    @Order(6)
    public void get_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        Long nonExistentId = 99999L;

        //Act
        ResponseEntity<ProblemDetail> response = this.testRestTemplate
                .getForEntity("/api/v1/orders/" + "{id}", ProblemDetail.class, nonExistentId);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Не найдено", response.getBody().getTitle());
        assertEquals("Заказ не найден", response.getBody().getDetail());
    }

    @Test
    @Order(7)
    public void update_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        Long nonExistentId = 9999L;
        OrderItemDto item = OrderItemDto.builder().productId(33).quantity(3).unit(Unit.KG).price(BigDecimal.ONE).productTitle("Product Title")
                .productTitle("Title").build();
        OrderDto orderDto = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.SHIPPED)
                .notes("Заказ обновлен: статус изменен")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();

        //Act
        ResponseEntity<ProblemDetail> response = this.testRestTemplate.exchange(
                "/api/v1/orders/" + nonExistentId,
                HttpMethod.PATCH,
                new HttpEntity<>(orderDto),
                ProblemDetail.class,
                nonExistentId
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Не найдено", response.getBody().getTitle());
    }

    @Test
    @Order(8)
    public void delete_WhenOrderExists_ShouldReturnOk() {
        //Arrange
        Long orderIdToDelete = 3L;

        //Act
        ResponseEntity<String> response = this.testRestTemplate
                .exchange(
                        "/api/v1/orders/" + orderIdToDelete,
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        String.class,
                        orderIdToDelete
                );

        //Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Заказ удалён", response.getBody());

        ResponseEntity<ProblemDetail> getResponse = this.testRestTemplate
                .getForEntity("/api/v1/orders/" + "/{id}", ProblemDetail.class, orderIdToDelete);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}