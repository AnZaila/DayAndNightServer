package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.ProfileVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProfileDTO(
        @NotBlank(message = "写一个名字") @Size(max = 40, message = "名字最多 40 个字") String displayName,
        @Size(max = 120, message = "签名最多 120 个字") String signature,
        @NotNull(message = "选择谁可以看") ProfileVisibility profileVisibility,
        @NotNull(message = "选择是否公开签名") Boolean showSignature,
        @NotNull(message = "选择是否公开背景") Boolean showCover,
        @NotNull(message = "选择是否公开邮箱") Boolean showEmail,
        @NotNull(message = "选择是否公开留言") Boolean showComments
) {
}
