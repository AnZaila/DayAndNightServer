package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDateTime;

public record ActivityVO(
        String kind,
        LocalDateTime createdAt,
        String actorName,
        String actorAvatarUrl,
        Long actorUserId,
        Long postId,
        String excerpt,
        String commentContent
) {
}
