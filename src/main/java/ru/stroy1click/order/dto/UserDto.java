package ru.stroy1click.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.stroy1click.order.model.Role;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {

    private final static Long SerialVersionUID= 1L;

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Boolean emailConfirmed;

    private Role role;
}
