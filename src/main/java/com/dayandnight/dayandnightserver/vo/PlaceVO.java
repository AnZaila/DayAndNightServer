package com.dayandnight.dayandnightserver.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlaceVO(
        Long id,
        LocalDate happenedAt,
        String location,
        String displayName,
        String coverUrl,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
