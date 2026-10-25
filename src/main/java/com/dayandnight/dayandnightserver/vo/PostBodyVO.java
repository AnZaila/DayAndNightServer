package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDate;

public record PostBodyVO(
        Long id,
        Long authorId,
        String displayName,
        String content,
        LocalDate happenedAt,
        String mood,
        String location,
        String visibility,
        String status,
        Boolean commentsOpen
) {
}
