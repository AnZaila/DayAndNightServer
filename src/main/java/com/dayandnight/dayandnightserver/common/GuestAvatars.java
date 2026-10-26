package com.dayandnight.dayandnightserver.common;

import com.dayandnight.dayandnightserver.common.exception.BadRequestException;

import java.util.regex.Pattern;

public final class GuestAvatars {

    private static final Pattern PATTERN = Pattern.compile(
            "^/uploads/avatar-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\.jpg$");

    private GuestAvatars() {
    }

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String url = value.trim();
        if (!PATTERN.matcher(url).matches()) {
            throw new BadRequestException("头像地址不对");
        }
        return url;
    }
}
