package com.dayandnight.dayandnightserver.common;

import com.dayandnight.dayandnightserver.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class CurrentUsers {

    private CurrentUsers() {
    }

    public static Optional<CurrentUser> optional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof OwnerUser owner) {
            return Optional.of(new CurrentUser(owner.getId(), owner.getUsername(), owner.getDisplayName()));
        }
        return Optional.empty();
    }

    public static CurrentUser require() {
        return optional().orElseThrow(() -> new UnauthorizedException("请先登录"));
    }
}
