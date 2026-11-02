package com.dayandnight.dayandnightserver.vo;

public record ProfileVO(
        Long id,
        String displayName,
        String avatarUrl,
        String coverUrl,
        String signature,
        String email,
        boolean open,
        boolean self,
        boolean showComments,
        boolean showLikes,
        String profileVisibility
) {
}
