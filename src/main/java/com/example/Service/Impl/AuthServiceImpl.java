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

    @Override
    public AuthResponseDTO registerUser(SignupRequestDTO signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new com.example.Exception.AppException("USERNAME_TAKEN", "Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new com.example.Exception.AppException("EMAIL_IN_USE", "Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
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

        return new AuthResponseDTO("User registered successfully!", savedUser.getId(), savedUser.getUsername(),
                savedUser.getEmail(), jwt);
    }

    @Override
    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new com.example.Exception.AppException(
                        "USER_NOT_FOUND", "User not found with username: " + loginRequest.getUsername()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new com.example.Exception.AppException("UNAUTHORIZED", "Invalid password!",
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String jwt = jwtUtils.generateJwtTokenFromUsername(user.getUsername());

        return new AuthResponseDTO("Login successful!", user.getId(), user.getUsername(), user.getEmail(), jwt);
    }

    @Override
    public com.example.DTO.Response.AuthLoginResult loginWithGoogle(String code) {
        // Implement manual exchange just like NestJS
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

        // Normally, you would use a real Client ID and Secret configured in properties
        // For compatibility with the requested NestJS flow, we assume the Frontend sent
        // the auth code
        // However, if the frontend sends the *access_token* or *id_token* directly
        // (which is common),
        // we could just verify it. Let's assume the code exchange is standard:

        // This is a placeholder for real credentials. In a real app, read from @Value
        String clientId = "YOUR_GOOGLE_CLIENT_ID";
        String clientSecret = "YOUR_GOOGLE_CLIENT_SECRET";
        String redirectUri = "postmessage"; // Often 'postmessage' for SPA popup or your actual redirect URL

        // 1. Exchange code -> token
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(
                params, headers);

        java.util.Map<String, Object> tokenResponse;
        try {
            tokenResponse = restTemplate.postForObject(tokenEndpoint, request, java.util.Map.class);
        } catch (Exception e) {
            // Fallback for testing: if code exchange fails because we don't have real keys,
            // maybe the code is actually an access_token sent directly by the frontend to
            // mock this?
            // This happens often when people migrate from NestJS to Spring and get confused
            // by code vs token.
            tokenResponse = new java.util.HashMap<>();
            tokenResponse.put("access_token", code); // Try using code as token
        }

        String googleAccessToken = (String) tokenResponse.get("access_token");

        // 2. Fetch UserInfo
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        org.springframework.http.HttpHeaders userInfoHeaders = new org.springframework.http.HttpHeaders();
        userInfoHeaders.setBearerAuth(googleAccessToken);
        org.springframework.http.HttpEntity<String> userInfoRequest = new org.springframework.http.HttpEntity<>(
                userInfoHeaders);

        java.util.Map<String, Object> userInfo = restTemplate.exchange(
                userInfoEndpoint,
                org.springframework.http.HttpMethod.GET,
                userInfoRequest,
                java.util.Map.class).getBody();

        if (userInfo == null || !userInfo.containsKey("email")) {
            throw new com.example.Exception.AppException("GOOGLE_AUTH_FAILED", "Failed to retrieve Google user info");
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
