package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.dto.LoginDTO;
import com.dayandnight.dayandnightserver.dto.UpdateProfileDTO;
import com.dayandnight.dayandnightserver.service.AuthService;
import com.dayandnight.dayandnightserver.vo.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public CurrentUserVO login(
            @Valid @RequestBody LoginDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.login(request, httpRequest, httpResponse);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authService.logout(httpRequest, httpResponse);
    }

    @GetMapping("/me")
    public CurrentUserVO me() {
        return authService.me();
    }

    @PatchMapping("/me")
    public CurrentUserVO update(
            @Valid @RequestBody UpdateProfileDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.updateProfile(request, httpRequest, httpResponse);
    }

    @PostMapping("/avatar")
    public CurrentUserVO avatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return authService.updateAvatar(file, httpRequest, httpResponse);
    }
}
