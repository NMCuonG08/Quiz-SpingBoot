package com.example.Service.Impl;

import com.example.DTO.Request.LoginRequestDTO;
import com.example.DTO.Request.SignupRequestDTO;
import com.example.DTO.Response.AuthResponseDTO;
import com.example.Entity.User;
import com.example.Repository.UserRepository;
import com.example.Security.JwtUtils;
import com.example.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.example.Entity.Role;
import com.example.Entity.UserRole;
import com.example.Repository.RoleRepository;
import com.example.Repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final com.example.Service.TokenBlacklistService tokenBlacklistService;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Override
    public AuthResponseDTO registerUser(SignupRequestDTO signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new com.example.Exception.AppException("EMAIL_IN_USE", "Email đã được sử dụng!");
        }

        // Auto-generate username từ phần trước @ của email
        String baseUsername = signupRequest.getEmail().split("@")[0];
        String username = baseUsername;
        // Nếu username đã tồn tại, thêm số random vào cuối
        if (userRepository.existsByUsername(username)) {
            username = baseUsername + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFull_name(signupRequest.getFull_name());
        user.setIsAdmin(false);

        User savedUser = userRepository.save(user);

        // Assign Default Role
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        UserRole userRole = new UserRole();
        userRole.setUserId(savedUser.getId());
        userRole.setRoleId(defaultRole.getId());
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);
        userRoleRepository.save(userRole);

        String jwt = jwtUtils.generateJwtTokenFromUsername(savedUser.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(savedUser.getUsername());

        return new AuthResponseDTO("Đăng ký thành công!", savedUser.getId(), savedUser.getUsername(),
                savedUser.getEmail(), jwt, refreshToken);
    }

    @Override
    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new com.example.Exception.AppException(
                        "UNAUTHORIZED", "Email hoặc mật khẩu không chính xác",
                        org.springframework.http.HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new com.example.Exception.AppException("UNAUTHORIZED", "Email hoặc mật khẩu không chính xác",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String jwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return new AuthResponseDTO("Login successful!", user.getId(), user.getUsername(), user.getEmail(), jwt,
                refreshToken);
    }

    @Override
    public void logout(String accessToken) {
        tokenBlacklistService.blacklistToken(accessToken);
    }

    @Override
    public java.util.Map<String, String> refreshAccessToken(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new com.example.Exception.AppException("INVALID_REFRESH_TOKEN",
                    "Refresh token không hợp lệ hoặc đã hết hạn",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new com.example.Exception.AppException("INVALID_REFRESH_TOKEN", "Refresh token đã bị thu hồi",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        String newAccessToken = jwtUtils.generateJwtTokenFromUsername(username);

        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("accessToken", newAccessToken);
        return result;
    }

    @Override
    public com.example.DTO.Response.AuthLoginResult loginWithGoogle(String code, String redirectUri) {
        // Implement manual exchange just like NestJS
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

        // Normally, you would use a real Client ID and Secret configured in properties
        // For compatibility with the requested NestJS flow, we assume the Frontend sent
        // the auth code
        // However, if the frontend sends the *access_token* or *id_token* directly
        // (which is common),
        // we could just verify it. Let's assume the code exchange is standard:

        // Use injected credentials
        String clientId = googleClientId;
        String clientSecret = googleClientSecret;

        // Use redirectUri from frontend if provided, otherwise fallback to postmessage
        String finalRedirectUri = (redirectUri != null && !redirectUri.isEmpty()) ? redirectUri : "postmessage";

        // DEBUG LOG - xem backend đang gửi gì tới Google
        System.out.println("=== [GOOGLE LOGIN DEBUG] ===");
        System.out.println(">>> Client ID    : " + clientId);
        System.out.println(">>> Client Secret: "
                + (clientSecret != null ? clientSecret.substring(0, Math.min(6, clientSecret.length())) + "..."
                        : "NULL"));
        System.out.println(">>> Redirect URI : " + finalRedirectUri);
        System.out.println(">>> Code (prefix): "
                + (code != null ? code.substring(0, Math.min(20, code.length())) + "..." : "NULL"));
        System.out.println("============================");

        // 1. Exchange code -> token
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", finalRedirectUri);
        params.add("grant_type", "authorization_code");

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(
                params, headers);

        java.util.Map<String, Object> tokenResponse;
        try {
            tokenResponse = restTemplate.postForObject(tokenEndpoint, request, java.util.Map.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            throw new com.example.Exception.AppException("GOOGLE_TOKEN_EXCHANGE_FAILED",
                    "Lỗi xác thực Google (Token Exchange): " + errorBody,
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new com.example.Exception.AppException("GOOGLE_AUTH_ERROR",
                    "Không thể kết nối tới Google: " + e.getMessage());
        }

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new com.example.Exception.AppException("GOOGLE_AUTH_FAILED", "Google không trả về access_token");
        }

        String googleAccessToken = (String) tokenResponse.get("access_token");

        // 2. Fetch UserInfo
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        java.util.Map<String, Object> userInfo;
        try {
            org.springframework.http.HttpHeaders userInfoHeaders = new org.springframework.http.HttpHeaders();
            userInfoHeaders.setBearerAuth(googleAccessToken);
            org.springframework.http.HttpEntity<String> userInfoRequest = new org.springframework.http.HttpEntity<>(
                    userInfoHeaders);

            userInfo = restTemplate.exchange(userInfoEndpoint, org.springframework.http.HttpMethod.GET, userInfoRequest,
                    java.util.Map.class).getBody();
        } catch (Exception e) {
            throw new com.example.Exception.AppException("GOOGLE_USER_INFO_FAILED",
                    "Không thể lấy thông tin User từ Google: " + e.getMessage());
        }

        if (userInfo == null || !userInfo.containsKey("email")) {
            throw new com.example.Exception.AppException("GOOGLE_AUTH_FAILED", "Dữ liệu Google trả về không có email");
        }

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 3. Find or Create User
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Create New Google User
            user = new User();
            user.setEmail(email);
            // Default username from email or name
            user.setUsername(email.split("@")[0] + "_" + java.util.UUID.randomUUID().toString().substring(0, 5));
            user.setFull_name(name);
            user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString())); // Random password
            user.setIsAdmin(false);

            user = userRepository.save(user);

            Role defaultRole = roleRepository.findByName("ROLE_USER").orElse(null);
            if (defaultRole != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(defaultRole.getId());
                userRole.setUser(user);
                userRole.setRole(defaultRole);
                userRoleRepository.save(userRole);
            }
        }

        // 4. Generate system tokens
        String systemAccessToken = jwtUtils.generateJwtTokenFromUsername(user.getUsername());
        // Currently your JwtUtils doesn't have a distinct generateRefreshToken,
        // so we'll generate another standard token to act as refresh token for now:
        String systemRefreshToken = jwtUtils.generateJwtTokenFromUsername(user.getUsername() + "-refresh");

        // 5. Construct user map for response (avoid sending heavy entity/password)
        java.util.Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("username", user.getUsername());
        userMap.put("fullName", user.getFull_name());
        userMap.put("avatar", picture);

        return com.example.DTO.Response.AuthLoginResult.builder()
                .user(userMap)
                .accessToken(systemAccessToken)
                .refreshToken(systemRefreshToken)
                .build();
    }
}
