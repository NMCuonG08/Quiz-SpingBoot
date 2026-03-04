package com.example.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.Security.JwtUtils;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * Đưa access token vào blacklist Redis.
     * TTL = thời gian còn lại của token để không tốn bộ nhớ vô ích.
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = jwtUtils.getExpirationFromToken(token);
            long ttlMs = expiration.getTime() - System.currentTimeMillis();
            if (ttlMs > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + token,
                        "logout",
                        Duration.ofMillis(ttlMs));
            }
        } catch (Exception ignored) {
            // token đã hết hạn -> không cần blacklist
        }
    }

    /**
     * Kiểm tra token có bị blacklist không.
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
