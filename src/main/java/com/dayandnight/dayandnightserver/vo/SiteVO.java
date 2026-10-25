package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDate;

public record SiteVO(
        String title,
        String subtitle,
        LocalDate togetherSince,
        long daysTogether,
        String about,
        String commentMode
) {
}
