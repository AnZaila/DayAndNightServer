package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDateTime;

public record CommentVO(
        Long id,
        Long postId,
        String nickname,
        String content,
        String status,
        LocalDateTime createdAt,
        String avatarUrl,
        Long parentId,
        String replyToNickname,
        Integer likeCount,
        Boolean liked,
        Long userId
) {
}
