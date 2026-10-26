package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.common.ClientAddresses;
import com.dayandnight.dayandnightserver.service.ProfileService;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import com.dayandnight.dayandnightserver.vo.ProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/api/users/{id}")
    public ProfileVO profile(@PathVariable long id) {
        return profileService.view(id);
    }

    @GetMapping("/api/users/{id}/comments")
    public PageResult<CommentVO> comments(
            @PathVariable long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        return profileService.comments(id, page, size, ClientAddresses.resolve(httpRequest));
    }

    @GetMapping("/api/users/{id}/posts")
    public PageResult<PostSummaryVO> posts(
            @PathVariable long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return profileService.posts(id, page, size);
    }
}
