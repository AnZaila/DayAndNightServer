package com.dayandnight.dayandnightserver.vo;

import java.util.List;

public record SiteStatsVO(
        Long posts,
        Long views,
        Long likes,
        Long comments,
        List<MonthStatVO> months,
        List<NamedCountVO> moods
) {
}
