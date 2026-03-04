package com.example.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank(message = "refreshToken is required")
    private String refreshToken;
}
