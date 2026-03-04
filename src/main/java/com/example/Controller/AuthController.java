package com.example.Controller;

import com.example.DTO.Request.LoginRequestDTO;
import com.example.DTO.Request.SignupRequestDTO;
import com.example.DTO.Response.AuthResponseDTO;
import com.example.Service.AuthService;
import com.example.Utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public AuthResponseDTO registerUser(
            @Valid @RequestBody SignupRequestDTO signupRequest,
            HttpServletResponse response) {
        AuthResponseDTO result = authService.registerUser(signupRequest);
        // Gửi refresh token qua httpOnly cookie
        cookieUtil.setRefreshTokenCookie(response, result.getRefreshToken());
        // Ẩn refresh token khỏi response body
        result.setRefreshToken(null);
        return result;
    }

    @PostMapping("/login")
    public AuthResponseDTO authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response) {
        AuthResponseDTO result = authService.authenticateUser(loginRequest);
        // Gửi refresh token qua httpOnly cookie
        cookieUtil.setRefreshTokenCookie(response, result.getRefreshToken());
        // Ẩn refresh token khỏi response body
        result.setRefreshToken(null);
        return result;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Blacklist access token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }

        // Blacklist refresh token trong cookie (nếu có)
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // Xóa cookie
        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        // Đọc refresh token từ cookie thay vì body
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new com.example.Exception.AppException("MISSING_REFRESH_TOKEN",
                    "Không tìm thấy refresh token",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> result = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/google")
    public java.util.Map<String, Object> loginWithGoogle(
            @Valid @RequestBody com.example.DTO.Request.GoogleLoginRequestDTO request,
            HttpServletResponse response) {

        com.example.DTO.Response.AuthLoginResult result = authService.loginWithGoogle(
                request.getCode(), request.getRedirectUri());

        // Gửi refresh token qua httpOnly cookie
        cookieUtil.setRefreshTokenCookie(response, result.getRefreshToken());

        java.util.Map<String, Object> responseBody = new java.util.HashMap<>();
        responseBody.put("user", result.getUser());
        responseBody.put("accessToken", result.getAccessToken());
        // Không trả refreshToken trong body
        return responseBody;
    }

    @PostMapping("/google/callback")
    public java.util.Map<String, Object> loginWithGoogleCallback(
            @Valid @RequestBody com.example.DTO.Request.GoogleLoginRequestDTO request,
            HttpServletResponse response) {
        return loginWithGoogle(request, response);
    }
}
