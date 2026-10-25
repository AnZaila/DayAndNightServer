package com.dayandnight.dayandnightserver.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PostBodyVO(
        Long id,
        Long authorId,
        String displayName,
        String content,
        LocalDateTime happenedAt,
        String mood,
        String weather,
        String location,
        String visibility,
        String status,
        Boolean commentsOpen,
        String authorAvatarUrl,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
