package com.dayandnight.dayandnightserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentDTO(
        @Size(max = 20, message = "昵称最多 20 个字") String nickname,
        @NotBlank(message = "写一点评论") @Size(max = 500, message = "评论最多 500 个字") String content
) {
}
