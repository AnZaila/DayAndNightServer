package com.dayandnight.dayandnightserver.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PostDetailVO(
        Long id,
        Long authorId,
        String displayName,
        String content,
        LocalDate happenedAt,
        String mood,
        String weather,
        String location,
        String visibility,
        String status,
        Boolean commentsOpen,
        List<MediaVO> media,
        String authorAvatarUrl,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
