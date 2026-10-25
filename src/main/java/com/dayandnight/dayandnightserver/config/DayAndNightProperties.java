package com.dayandnight.dayandnightserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

@ConfigurationProperties(prefix = "dayandnight")
public record DayAndNightProperties(
        String webOrigin,
        String uploadDir,
        LocalDate togetherSince,
        Owner ownerA,
        Owner ownerB
) {
    public record Owner(String email, String password, String name) {
    }
}
