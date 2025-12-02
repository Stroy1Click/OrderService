package ru.stroy1click.order.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.order.model.OrderStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    @NotNull(message = "{validate.orderdto.id.not_null}")
    private Long id;

    private String notes;

    @Min(value = 1, message = "{validate.orderdto.quantity.min}")
    @NotNull(message = "{validate.orderdto.quantity.not_null}")
    private Integer quantity;

    @NotNull(message = "{validate.orderdto.order_status.not_null}")
    private OrderStatus orderStatus;

    @NotNull(message = "{validate.orderdto.created_at.not_null}")
    private LocalDateTime createdAt;

    @NotNull(message = "{validate.orderdto.updated_at.not_null}")
    private LocalDateTime updatedAt;

    @Min(value = 1, message = "{validate.orderdto.product_id.min}")
    @NotNull(message = "{validate.orderdto.product_id.not_null}")
    private Integer productId;

    @NotNull(message = "{validate.orderdto.contact_phone.not_null}")
    @Pattern(regexp = "^(\\\\+7|8)\\\\d{10}$", message = "{validate.orderdto.contact_phone.pattern}")
    private String contactPhone;

    @Positive(message = "{validate.orderdto.user_id.min}")
    @NotNull(message = "{validate.orderdto.user_id.not_null}")
    private Long userId;
}
