package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.service.AlbumService;
import com.dayandnight.dayandnightserver.vo.AlbumPhotoVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public PageResult<AlbumPhotoVO> list(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "24") int size) {
        return albumService.list(page, size);
    }

    @PostMapping
    public AlbumPhotoVO upload(@RequestParam("file") MultipartFile file) {
        return albumService.upload(file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        albumService.delete(id);
    }
}
