package org.example.generator_password.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder

public class PasswordStrengthResponse {
    private int score;
    private String label;
    private List<String> suggestions;
}
