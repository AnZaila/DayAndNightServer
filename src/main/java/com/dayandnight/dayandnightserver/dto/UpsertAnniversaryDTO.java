package com.dayandnight.dayandnightserver.dto;

import com.dayandnight.dayandnightserver.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpsertAnniversaryDTO(
        @NotBlank(message = "填写纪念日名称") @Size(max = 40) String title,
        @NotNull(message = "填写日期") LocalDate date,
        @NotNull Boolean repeatYearly,
        @Size(max = 200) String note,
        @NotNull(message = "选择谁可以看") Visibility visibility
) {
}
