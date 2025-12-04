package ru.stroy1click.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;

    @Min(value = 1, message = "{validate.order_item_dto.product_id.min}")
    @NotNull(message = "{validate.order_item_dto.product_id.not_null}")
    private Integer productId;

    @Min(value = 1, message = "{validate.order_item_dto.quantity.min}")
    @NotNull(message = "{validate.order_item_dto.quantity.not_null}")
    private Integer quantity;
}

