package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.LoginDTO;
import com.dayandnight.dayandnightserver.dto.UpdateProfileDTO;
import com.dayandnight.dayandnightserver.vo.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    CurrentUserVO login(LoginDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    CurrentUserVO me();

    CurrentUserVO updateProfile(UpdateProfileDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    CurrentUserVO updateAvatar(MultipartFile file, HttpServletRequest httpRequest, HttpServletResponse httpResponse);
}
