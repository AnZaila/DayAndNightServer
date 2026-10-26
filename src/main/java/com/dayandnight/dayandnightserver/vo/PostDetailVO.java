package com.dayandnight.dayandnightserver.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailVO(
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
        List<MediaVO> media,
        String authorAvatarUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        Boolean liked,
        Boolean pinned
) {
}
