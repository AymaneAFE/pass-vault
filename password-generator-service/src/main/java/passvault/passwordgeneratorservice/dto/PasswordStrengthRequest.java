package org.example.generator_password.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordStrengthRequest {
    @NotBlank
    private String password;
}
