package com.example.Utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {

    @Value("${app.production:false}")
    private boolean isProduction;

    @Value("${app.cookie.domain:localhost}")
    private String domain;

    private static final String REFRESH_TOKEN_COOKIE = "__refreshToken";
    private static final int MAX_AGE_SECONDS = 30 * 24 * 60 * 60; // 30 days

    /**
     * Gửi refresh token qua httpOnly cookie - giống NestJS
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction); // true khi production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE_SECONDS);

        // SameSite phải set qua header vì Jakarta Cookie không hỗ trợ trực tiếp
        String sameSite = isProduction ? "None" : "Lax";
        String cookieHeader = String.format(
                "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=%s%s%s",
                REFRESH_TOKEN_COOKIE,
                refreshToken,
                MAX_AGE_SECONDS,
                sameSite,
                isProduction ? "; Secure" : "",
                (!domain.equals("localhost") ? "; Domain=" + domain : ""));

        response.addHeader("Set-Cookie", cookieHeader);
    }

    /**
     * Xóa cookie khi logout
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        String cookieHeader = String.format(
                "%s=; Max-Age=0; Path=/; HttpOnly; SameSite=%s%s%s",
                REFRESH_TOKEN_COOKIE,
                isProduction ? "None" : "Lax",
                isProduction ? "; Secure" : "",
                (!domain.equals("localhost") ? "; Domain=" + domain : ""));
        response.addHeader("Set-Cookie", cookieHeader);
    }

    /**
     * Đọc refresh token từ cookie request
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
