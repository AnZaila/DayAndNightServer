package com.dayandnight.dayandnightserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileDTO(
        @NotBlank(message = "写一个名字") @Size(max = 40, message = "名字最多 40 个字") String displayName,
        @Size(max = 120, message = "签名最多 120 个字") String signature
) {
}
