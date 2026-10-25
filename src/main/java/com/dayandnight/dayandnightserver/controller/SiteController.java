package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.dto.UpdateSiteDTO;
import com.dayandnight.dayandnightserver.service.SiteService;
import com.dayandnight.dayandnightserver.vo.SiteVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public SiteVO current() {
        return siteService.current();
    }

    @PatchMapping
    public SiteVO update(@Valid @RequestBody UpdateSiteDTO request) {
        return siteService.update(request);
    }
}
