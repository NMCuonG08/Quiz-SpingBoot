package com.example.Controller;

import com.example.DTO.Request.LoginRequestDTO;
import com.example.DTO.Request.SignupRequestDTO;
import com.example.DTO.Response.AuthResponseDTO;
import com.example.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public AuthResponseDTO registerUser(@Valid @RequestBody SignupRequestDTO signupRequest) {
        return authService.registerUser(signupRequest);
    }

    @PostMapping("/login")
    public AuthResponseDTO authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/google")
    public java.util.Map<String, Object> loginWithGoogle(
            @Valid @RequestBody com.example.DTO.Request.GoogleLoginRequestDTO request,
            jakarta.servlet.http.HttpServletResponse response) {

        com.example.DTO.Response.AuthLoginResult result = authService.loginWithGoogle(request.getCode());

        // Create refresh token cookie (using same logic from NestJS example)
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("__refreshToken",
                result.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true in production
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days in seconds

        response.addCookie(cookie);

        java.util.Map<String, Object> responseBody = new java.util.HashMap<>();
        responseBody.put("user", result.getUser());
        responseBody.put("accessToken", result.getAccessToken());

        return responseBody;
    }

    @PostMapping("/google/callback")
    public java.util.Map<String, Object> loginWithGoogleCallback(
            @Valid @RequestBody com.example.DTO.Request.GoogleLoginRequestDTO request,
            jakarta.servlet.http.HttpServletResponse response) {
        // Points to the same logic as NestJS compatibility route
        return loginWithGoogle(request, response);
    }
}
