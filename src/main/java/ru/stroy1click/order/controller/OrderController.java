package ru.stroy1click.order.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.stroy1click.order.dto.OrderDto;
import ru.stroy1click.order.exception.ValidationException;
import ru.stroy1click.order.mapper.OrderItemMapper;
import ru.stroy1click.order.service.OrderService;
import ru.stroy1click.order.util.ValidationErrorUtils;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Взаимодействие с заказами")
@RateLimiter(name = "orderLimiter")
public class OrderController {

    private final MessageSource messageSource;

    private final OrderService orderService;

    private final OrderItemMapper orderItemMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Получение заказа")
    public OrderDto get(@PathVariable("id") Long id){
        return this.orderService.get(id);
    }

    @GetMapping("/user")
    @Operation(summary = "Получение всех заказов пользователя")
    public List<OrderDto> getByUserId(@RequestParam("userId") Long userId){
        return this.orderService.getByUserId(userId);
    }

    @PostMapping
    @Operation(summary = "Создание заказа")
    public ResponseEntity<String> create(@RequestBody @Valid OrderDto orderDto,
                                         BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.orderService.create(orderDto);

        return ResponseEntity.ok(
                this.messageSource.getMessage(
                        "info.order.create",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление заказа")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestBody @Valid OrderDto orderDto,
                                         BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.orderService.update(id, orderDto);

        return ResponseEntity.ok(
                this.messageSource.getMessage(
                        "info.order.update",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление заказа")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        this.orderService.delete(id);

        return ResponseEntity.ok(
                this.messageSource.getMessage(
                        "info.order.delete",
                        null,
                        Locale.getDefault()
                )
        );
    }

}
