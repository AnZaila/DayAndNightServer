package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.service.StatsService;
import com.dayandnight.dayandnightserver.vo.SiteStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/api/stats")
    public SiteStatsVO view() {
        return statsService.view();
    }
}
