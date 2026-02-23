package ru.stroy1click.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.common.dto.Unit;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;

    @Min(value = 1, message = "{validate.order_item_dto.product_id.min}")
    @NotNull(message = "{validate.order_item_dto.product_id.not_null}")
    private Integer productId;

    @NotBlank(message = "{validate.order_item_dto.product_title.not_blank}")
    private String productTitle;

    @DecimalMin(value = "1.0", message = "{validate.order_item_dto.price.min}")
    @NotNull(message = "{validate.order_item_dto.price.not_null}")
    private BigDecimal price;

    @Min(value = 1, message = "{validate.order_item_dto.quantity.min}")
    @NotNull(message = "{validate.order_item_dto.quantity.not_null}")
    private Integer quantity;

    @NotNull(message = "{validate.order_item_dto.unit.not_null}")
    private Unit unit;
}