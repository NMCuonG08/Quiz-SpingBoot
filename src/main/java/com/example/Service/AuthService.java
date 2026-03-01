package com.example.Service;

import com.example.DTO.Request.LoginRequestDTO;
import com.example.DTO.Request.SignupRequestDTO;
import com.example.DTO.Response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO registerUser(SignupRequestDTO signupRequest);

    AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest);

    com.example.DTO.Response.AuthLoginResult loginWithGoogle(String code);
}
