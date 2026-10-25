package com.dayandnight.dayandnightserver.vo;

import java.util.List;

public record PageResult<T>(List<T> items, long total, int page, int size, boolean hasMore) {

    public static <T> PageResult<T> of(List<T> items, long total, int page, int size) {
        return new PageResult<>(items, total, page, size, (long) page * size < total);
    }
}
