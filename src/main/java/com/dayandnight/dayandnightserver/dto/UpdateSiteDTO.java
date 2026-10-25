package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.CommentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateSiteDTO(
        @NotBlank(message = "填写站点标题") @Size(max = 40) String title,
        @NotBlank(message = "填写一行小字") @Size(max = 80) String subtitle,
        @NotNull(message = "填写在一起的日期") LocalDate togetherSince,
        @NotNull @Size(max = 5000, message = "关于页太长了") String about,
        @NotNull(message = "选择评论方式") CommentMode commentMode
) {
}
