package com.example.Service;

import com.example.DTO.Request.LoginRequestDTO;
import com.example.DTO.Request.SignupRequestDTO;
import com.example.DTO.Response.AuthResponseDTO;

import java.util.Map;

public interface AuthService {
    AuthResponseDTO registerUser(SignupRequestDTO signupRequest);

    AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest);

    com.example.DTO.Response.AuthLoginResult loginWithGoogle(String code, String redirectUri);

    void logout(String accessToken);

    Map<String, String> refreshAccessToken(String refreshToken);
}
