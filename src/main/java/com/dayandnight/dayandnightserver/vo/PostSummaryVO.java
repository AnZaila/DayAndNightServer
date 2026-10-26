package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDateTime;

public record PostSummaryVO(
        Long id,
        Long authorId,
        LocalDateTime happenedAt,
        String excerpt,
        String mood,
        String weather,
        String visibility,
        String displayName,
        String coverUrl,
        String authorAvatarUrl,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        Boolean liked,
        Boolean pinned
) {
}
