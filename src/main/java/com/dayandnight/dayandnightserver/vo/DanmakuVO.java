package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDateTime;

public record DanmakuVO(
        Long id,
        Long postId,
        String nickname,
        String content,
        String status,
        LocalDateTime createdAt,
        Long parentId,
        String replyToNickname,
        Integer likeCount,
        Boolean liked,
        String avatarUrl,
        Long userId
) {
}
