package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDate;

public record PostSummaryVO(
        Long id,
        Long authorId,
        LocalDate happenedAt,
        String excerpt,
        String mood,
        String visibility,
        String displayName,
        String coverUrl
) {
}
