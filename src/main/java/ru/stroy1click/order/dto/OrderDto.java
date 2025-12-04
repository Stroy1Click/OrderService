package ru.stroy1click.order.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.stroy1click.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    @NotNull(message = "{validate.orderdto.id.not_null}")
    private Long id;

    @Length(max = 10_000, message = "{}")
    private String notes;

    @NotNull(message = "{validate.orderdto.order_status.not_null}")
    private OrderStatus orderStatus;

    @NotNull(message = "{validate.orderdto.created_at.not_null}")
    private LocalDateTime createdAt;

    @NotNull(message = "{validate.orderdto.updated_at.not_null}")
    private LocalDateTime updatedAt;

    @Valid
    @NotNull(message = "{validate.orderdto.product_id.not_null}")
    private List<OrderItemDto> orderItems;

    @NotNull(message = "{validate.orderdto.contact_phone.not_null}")
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "{validate.orderdto.contact_phone.pattern}")
    private String contactPhone;

    @Positive(message = "{validate.orderdto.user_id.min}")
    @NotNull(message = "{validate.orderdto.user_id.not_null}")
    private Long userId;
}
