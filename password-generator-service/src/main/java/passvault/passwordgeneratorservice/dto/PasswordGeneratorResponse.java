package org.example.generator_password.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PasswordGeneratorResponse {
    private String password;
    private int strength;
    private String strengthLabel;
}
