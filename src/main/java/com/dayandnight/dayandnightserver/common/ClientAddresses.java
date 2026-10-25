package com.dayandnight.dayandnightserver.common;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientAddresses {

    private ClientAddresses() {
    }

    public static String resolve(HttpServletRequest request) {
        String remote = normalize(request.getRemoteAddr());
        if (isLoopback(remote)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                String first = forwarded.split(",")[0].trim();
                if (!first.isEmpty()) {
                    return normalize(first);
                }
            }
        }
        return remote == null ? "unknown" : remote;
    }

    private static String normalize(String address) {
        if (address == null || address.isBlank()) {
            return "unknown";
        }
        String value = address.trim();
        if (value.startsWith("::ffff:")) {
            return value.substring(7);
        }
        return value;
    }

    private static boolean isLoopback(String address) {
        return "127.0.0.1".equals(address) || "::1".equals(address) || "0:0:0:0:0:0:0:1".equals(address);
    }
}
