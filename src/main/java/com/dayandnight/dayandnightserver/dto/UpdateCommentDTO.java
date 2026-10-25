package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.CommentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentDTO(@NotNull(message = "选择评论状态") CommentStatus status) {
}
