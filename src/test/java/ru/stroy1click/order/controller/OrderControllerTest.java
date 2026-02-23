package ru.stroy1click.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.stroy1click.common.dto.LegalForm;
import ru.stroy1click.common.dto.OrderStatus;
import ru.stroy1click.common.dto.Unit;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.dto.OrderItemDto;
import ru.stroy1click.order.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    public void create_WhenOrderDtoContactPhoneIsInvalid_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+799")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Номер телефона должен быть валидным", problemDetail.getDetail());
    }

    @Test
    public void create_WhenOrderDtoUserIdIsNonPositive_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
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
                .userId(-600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Id пользователя не может быть меньше 1", problemDetail.getDetail());
    }

    @Test
    public void create_WhenOrderDtoQuantityIsZero_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Количество единиц товара не может быть пустым", problemDetail.getDetail());
    }

    @Test
    public void create_WhenOrderDtoInnIsEmptyShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+799")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("ИНН не может быть пустым"));
    }

    @Test
    public void create_WhenOrderDtoLegalFormIsEmpty_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .userId(-600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("Тип компании не может быть пустым"));
    }

    @Test
    public void create_WhenOrderDtoContactEmailIsEmpty_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/orders")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("Контактная электронная почта не может быть пустой"));
    }

    @Test
    public void update_WhenOrderDtoContactPhoneIsInvalid_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+799")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Номер телефона должен быть валидным", problemDetail.getDetail());
    }

    @Test
    public void update_WhenOrderDtoUserIdIsNonPositive_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
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
                .userId(-600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Id пользователя не может быть меньше 1", problemDetail.getDetail());
    }

    @Test
    public void update_WhenOrderDtoQuantityIsZero_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
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
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Количество единиц товара не может быть пустым", problemDetail.getDetail());
    }

    @Test
    public void update_WhenOrderDtoInnIsEmptyShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+799")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("ИНН не может быть пустым"));
    }

    @Test
    public void update_WhenOrderDtoLegalFormIsEmpty_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).quantity(1).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .contactEmail("contactemail@gmail.com")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .userId(-600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("Тип компании не может быть пустым"));
    }

    @Test
    public void update_WhenOrderDtoContactEmailIsEmpty_ShouldThrowValidationException() throws Exception {
        //Arrange
        OrderItemDto item = OrderItemDto.builder().productId(10).productTitle("Title").price(BigDecimal.ONE).unit(Unit.KG).build();
        OrderDto invalidPhoneOrder = OrderDto.builder()
                .inn("1234567890")
                .kpp("123456789")
                .orderStatus(OrderStatus.CREATED)
                .notes("Новый заказ для тестирования")
                .contactPhone("+79999999999")
                .contactName("Contact Name")
                .deliveryAddress("Delivery Address")
                .legalName("Company")
                .legalForm(LegalForm.LLC)
                .userId(600L)
                .orderItems(List.of(item))
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/orders/1")
                .content(new ObjectMapper().writeValueAsString(invalidPhoneOrder))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertTrue(problemDetail.getDetail().contains("Контактная электронная почта не может быть пустой"));
    }
}