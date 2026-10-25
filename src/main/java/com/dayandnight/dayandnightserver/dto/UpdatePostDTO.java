package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.PostStatus;
import com.dayandnight.dayandnightserver.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePostDTO(
        @NotBlank(message = "写一点内容") @Size(max = 20000, message = "正文太长了") String content,
        @NotNull(message = "选一个发生日期") LocalDate happenedAt,
        @Size(max = 32, message = "心情太长了") String mood,
        @Size(max = 80, message = "地点太长了") String location,
        @NotNull(message = "选择谁可以看") Visibility visibility,
        @NotNull(message = "选择是否公开在时间轴") PostStatus status,
        @NotNull(message = "选择是否允许评论") Boolean commentsOpen
) {
}
