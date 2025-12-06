package ru.stroy1click.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {

    private final static Long SerialVersionUID= 1L;

    private Integer id;

    private String title;

    private String description;

    private Double price;

    private Boolean inStock;

    private Integer categoryId;

    private Integer subcategoryId;

    private Integer productTypeId;
}
