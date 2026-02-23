package ru.stroy1click.order.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.stroy1click.common.dto.LegalForm;
import ru.stroy1click.common.dto.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long id;

    @Length(min = 5, max = 100, message = "{validate.orderdto.legal_name.length}")
    @NotBlank(message = "{validate.orderdto.legal_name.not_blank}")
    private String legalName;

    @NotNull(message = "{validate.orderdto.legal_form.not_null}")
    private LegalForm legalForm;

    @Length(max = 10_000, message = "{validate.orderdto.notes.length}")
    private String notes;

    @Length(min = 5, max = 100, message = "{validate.orderdto.delivery_address.length}")
    @NotBlank(message = "{validate.orderdto.delivery_address.not_blank}")
    private String deliveryAddress;

    @Pattern(regexp = "^(\\d{10}|\\d{12})$",message = "{validate.orderdto.inn.pattern}")
    @NotBlank(message = "{validate.orderdto.inn.not_blank}")
    private String inn;

    @Pattern(regexp = "^(\\d{9})?$", message = "{validate.orderdto.kpp.pattern}")
    private String kpp;

    @NotNull(message = "{validate.orderdto.order_status.not_null}")
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Valid
    @NotNull(message = "{validate.orderdto.product_id.not_null}")
    private List<OrderItemDto> orderItems;

    @Length(min = 5, max = 100, message = "{validate.orderdto.contact_name.length}")
    @NotBlank(message = "{validate.orderdto.contact_name.not_blank}")
    private String contactName;

    @NotNull(message = "{validate.orderdto.contact_phone.not_null}")
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "{validate.orderdto.contact_phone.pattern}")
    private String contactPhone;

    @Length(min = 5, max = 100, message = "{validate.orderdto.contact_email.length}")
    @NotBlank(message = "{validate.orderdto.contact_email.not_blank}")
    @Email(message = "{validate.orderdto.contact_email.valid}")
    private String contactEmail;

    @Positive(message = "{validate.orderdto.user_id.min}")
    @NotNull(message = "{validate.orderdto.user_id.not_null}")
    private Long userId;
}