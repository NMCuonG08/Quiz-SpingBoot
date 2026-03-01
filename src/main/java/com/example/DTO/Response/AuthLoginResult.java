package com.example.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResult {
    private Map<String, Object> user;
    private String accessToken;
    private String refreshToken;
}
