package com.dayandnight.dayandnightserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDanmakuDTO(
        @Size(max = 20, message = "昵称最多 20 个字") String nickname,
        @NotBlank(message = "写一条弹幕") @Size(max = 40, message = "弹幕最多 40 个字") String content,
        @Size(max = 80, message = "头像地址不对") String avatarUrl
) {
}
