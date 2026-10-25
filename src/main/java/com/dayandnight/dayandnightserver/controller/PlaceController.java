package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.service.PostService;
import com.dayandnight.dayandnightserver.vo.PlaceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlaceController {

    private final PostService postService;

    public PlaceController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/places")
    public List<PlaceVO> list() {
        return postService.places();
    }
}
