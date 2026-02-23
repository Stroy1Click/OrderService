package ru.stroy1click.order.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.common.dto.Unit;

import java.math.BigDecimal;

@Data
@Table(schema = "ordering", name = "order_items")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer productId;

    private String productTitle;

    private BigDecimal price;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
