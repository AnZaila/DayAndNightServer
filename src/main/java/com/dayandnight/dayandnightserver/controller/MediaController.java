package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.service.MediaService;
import com.dayandnight.dayandnightserver.vo.ImageVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public ImageVO upload(@RequestParam("file") MultipartFile file) {
        return mediaService.storeContent(file);
    }
}
