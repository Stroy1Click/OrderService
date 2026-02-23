package ru.stroy1click.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.stroy1click.common.dto.LegalForm;
import ru.stroy1click.common.dto.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(schema = "ordering", name = "orders")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String legalName;

    @Enumerated(EnumType.STRING)
    private LegalForm legalForm;

    private String notes;

    private String deliveryAddress;

    private String inn;

    private String kpp;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private Long userId;

    /**
     * OrderItem существует только внутри Order и никогда не используется отдельно
     * cascade = ALL + orphanRemoval = true. Это удобнее и проще.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
