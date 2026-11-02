package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDateTime;

public record AlbumPhotoVO(
        Long id,
        String kind,
        String url,
        Integer width,
        Integer height,
        Long postId,
        LocalDateTime takenAt) {
}
