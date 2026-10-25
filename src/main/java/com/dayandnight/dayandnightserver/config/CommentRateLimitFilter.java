package com.dayandnight.dayandnightserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommentRateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 8;
    private static final long WINDOW_MILLIS = 60_000;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean commentPost = "POST".equals(request.getMethod()) && uri.matches(".*/api/posts/\\d+/comments");
        if (commentPost && !allow(hash(request.getRemoteAddr()))) {
            response.setStatus(429);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"title\":\"太频繁\",\"status\":429,\"detail\":\"评论太频繁，请稍后再试\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean allow(String key) {
        long now = System.currentTimeMillis();
        Window window = windows.compute(key, (ignored, current) -> {
            if (current == null || now - current.startedAt > WINDOW_MILLIS) {
                return new Window(now, 1);
            }
            current.count += 1;
            return current;
        });
        return window.count <= LIMIT;
    }

    private String hash(String remoteAddress) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(("dayandnight:" + remoteAddress).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            return "unknown";
        }
    }

    private static final class Window {
        private final long startedAt;
        private int count;

        private Window(long startedAt, int count) {
            this.startedAt = startedAt;
            this.count = count;
        }
    }
}
