package com.dayandnight.dayandnightserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "请填写邮箱") @Email(message = "邮箱格式不正确") String email,
        @NotBlank(message = "请填写密码") String password
) {
}
