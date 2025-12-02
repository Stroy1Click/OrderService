package ru.stroy1click.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.order.model.OrderStatus;

import java.time.LocalDateTime;

@Data
@Table(schema = "product", name = "orders")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notes;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer productId;

    private String contactPhone;

    private Long userId;
}
