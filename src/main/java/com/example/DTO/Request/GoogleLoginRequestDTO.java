package com.example.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequestDTO {
    @NotBlank(message = "code is required")
    private String code;

    private String redirectUri;
}
