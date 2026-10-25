package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.dto.UpsertAnniversaryDTO;
import com.dayandnight.dayandnightserver.service.AnniversaryService;
import com.dayandnight.dayandnightserver.vo.AnniversaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/anniversaries")
public class AnniversaryController {

    private final AnniversaryService anniversaryService;

    public AnniversaryController(AnniversaryService anniversaryService) {
        this.anniversaryService = anniversaryService;
    }

    @GetMapping
    public List<AnniversaryVO> list() {
        return anniversaryService.list();
    }

    @PostMapping
    public AnniversaryVO create(@Valid @RequestBody UpsertAnniversaryDTO request) {
        return anniversaryService.create(request);
    }

    @PatchMapping("/{id}")
    public AnniversaryVO update(@PathVariable long id, @Valid @RequestBody UpsertAnniversaryDTO request) {
        return anniversaryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        anniversaryService.delete(id);
    }
}
