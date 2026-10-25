package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePostDTO(
        @NotBlank(message = "写一点内容") @Size(max = 20000, message = "正文太长了") String content,
        @NotNull(message = "选一个发生时间") LocalDateTime happenedAt,
        @Size(max = 32, message = "心情太长了") String mood,
        @Size(max = 16, message = "天气太长了") String weather,
        @Size(max = 80, message = "地点太长了") String location,
        @NotNull(message = "选择谁可以看") Visibility visibility,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
