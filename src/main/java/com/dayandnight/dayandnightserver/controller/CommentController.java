package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.dto.CreateCommentDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.service.CommentService;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentVO> list(@PathVariable long postId) {
        return commentService.listForPost(postId);
    }

    @PostMapping("/api/posts/{postId}/comments")
    public CommentVO create(
            @PathVariable long postId,
            @Valid @RequestBody CreateCommentDTO request,
            HttpServletRequest httpRequest) {
        return commentService.create(postId, request, httpRequest.getRemoteAddr());
    }

    @GetMapping("/api/comments")
    public List<CommentVO> recent() {
        return commentService.recent();
    }

    @PatchMapping("/api/comments/{id}")
    public CommentVO update(@PathVariable long id, @Valid @RequestBody UpdateCommentDTO request) {
        return commentService.update(id, request);
    }

    @DeleteMapping("/api/comments/{id}")
    public void delete(@PathVariable long id) {
        commentService.delete(id);
    }
}
