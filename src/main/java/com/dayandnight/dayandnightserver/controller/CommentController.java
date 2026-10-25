package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.common.ClientAddresses;
import com.dayandnight.dayandnightserver.dto.CreateCommentDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.service.CommentService;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/posts/{postId}/comments")
    public PageResult<CommentVO> list(
            @PathVariable long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        return commentService.listForPost(postId, page, size, ClientAddresses.resolve(httpRequest));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public CommentVO create(
            @PathVariable long postId,
            @Valid @RequestBody CreateCommentDTO request,
            HttpServletRequest httpRequest) {
        return commentService.create(postId, request, ClientAddresses.resolve(httpRequest));
    }

    @GetMapping("/api/comments")
    public PageResult<CommentVO> recent(
            @RequestParam(defaultValue = "manage") String scope,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        return commentService.recent(scope, page, size, ClientAddresses.resolve(httpRequest));
    }

    @PostMapping("/api/comments/{id}/like")
    public CommentVO like(@PathVariable long id, HttpServletRequest httpRequest) {
        return commentService.toggleLike(id, ClientAddresses.resolve(httpRequest));
    }

    @PatchMapping("/api/comments/{id}")
    public CommentVO update(@PathVariable long id, @Valid @RequestBody UpdateCommentDTO request, HttpServletRequest httpRequest) {
        return commentService.update(id, request, ClientAddresses.resolve(httpRequest));
    }

    @DeleteMapping("/api/comments/{id}")
    public void delete(@PathVariable long id) {
        commentService.delete(id);
    }
}
