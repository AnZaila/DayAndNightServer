package com.dayandnight.dayandnightserver.vo;

import java.time.LocalDate;

public record AnniversaryVO(
        Long id,
        String title,
        LocalDate date,
        Boolean repeatYearly,
        String note,
        String visibility
) {
}
