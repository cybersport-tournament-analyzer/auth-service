package com.vkr.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    @Length(min = 4, max = 128, message = "Username must be between 4 and 128 characters length")
    private String username;

    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is in invalid format")
    @Length(max = 128, message = "Email must be less than 128 characters length")
    private String email;

    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    @Length(min = 8, max = 128, message = "Password must be between 8 and 128 characters length")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,128}$", message = "Password must contain at least one uppercase and lowercase characters and a digit")
    private String password;
}
