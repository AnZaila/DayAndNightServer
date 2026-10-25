package com.dayandnight.dayandnightserver.common;

public final class Pages {

    private Pages() {
    }

    public static int page(int page) {
        return Math.max(page, 1);
    }

    public static int size(int size, int fallback) {
        if (size < 1) {
            return fallback;
        }
        return Math.min(size, 40);
    }

    public static int offset(int page, int size) {
        return (page - 1) * size;
    }
}
