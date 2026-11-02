package com.dayandnight.dayandnightserver.vo;

public record CurrentUserVO(
        Long id,
        String email,
        String displayName,
        String avatarUrl,
        String coverUrl,
        String signature,
        String profileVisibility,
        boolean showSignature,
        boolean showCover,
        boolean showEmail,
        boolean showComments,
        boolean showLikes
) {
}
