package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.dto.CreatePostDTO;
import com.dayandnight.dayandnightserver.dto.UpdatePostDTO;
import com.dayandnight.dayandnightserver.service.PostService;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import com.dayandnight.dayandnightserver.vo.PostDetailVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostSummaryVO> list() {
        return postService.list();
    }

    @GetMapping("/{id}")
    public PostDetailVO get(@PathVariable long id) {
        return postService.get(id);
    }

    @PostMapping
    public PostDetailVO create(@Valid @RequestBody CreatePostDTO request) {
        return postService.create(request);
    }

    @PatchMapping("/{id}")
    public PostDetailVO update(@PathVariable long id, @Valid @RequestBody UpdatePostDTO request) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        postService.delete(id);
    }

    @PostMapping("/{id}/media")
    public List<MediaVO> addMedia(@PathVariable long id, @RequestParam("files") List<MultipartFile> files) {
        return postService.addMedia(id, files);
    }

    @DeleteMapping("/{id}/media/{mediaId}")
    public void deleteMedia(@PathVariable long id, @PathVariable long mediaId) {
        postService.deleteMedia(id, mediaId);
    }
}
