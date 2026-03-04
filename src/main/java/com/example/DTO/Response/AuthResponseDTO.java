package com.example.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String message;
    private UUID userId;
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
}
