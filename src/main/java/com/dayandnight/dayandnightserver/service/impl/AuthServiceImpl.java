package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.OwnerUser;
import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.UnauthorizedException;
import com.dayandnight.dayandnightserver.config.DayAndNightProperties;
import com.dayandnight.dayandnightserver.dto.LoginDTO;
import com.dayandnight.dayandnightserver.dto.UpdateProfileDTO;
import com.dayandnight.dayandnightserver.mapper.UserMapper;
import com.dayandnight.dayandnightserver.pojo.User;
import com.dayandnight.dayandnightserver.service.AuthService;
import com.dayandnight.dayandnightserver.vo.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int AVATAR_EDGE = 512;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserMapper userMapper;
    private final DayAndNightProperties properties;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            UserMapper userMapper,
            DayAndNightProperties properties) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userMapper = userMapper;
        this.properties = properties;
    }

    @Override
    public CurrentUserVO login(LoginDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String email = request.email().trim().toLowerCase();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, request.password()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            new ChangeSessionIdAuthenticationStrategy().onAuthentication(authentication, httpRequest, httpResponse);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);
            OwnerUser owner = (OwnerUser) authentication.getPrincipal();
            return new CurrentUserVO(owner.getId(), owner.getUsername(), owner.getDisplayName(), owner.getAvatarUrl());
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("邮箱或密码不正确");
        }
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        SecurityContextHolder.clearContext();
        if (httpRequest.getSession(false) != null) {
            httpRequest.getSession(false).invalidate();
        }
    }

    @Override
    public CurrentUserVO me() {
        return toVo(stored());
    }

    @Override
    public CurrentUserVO updateProfile(
            UpdateProfileDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = stored();
        user.setDisplayName(request.displayName().trim());
        userMapper.updateProfile(user.getId(), user.getDisplayName(), user.getAvatarUrl());
        remember(user, httpRequest, httpResponse);
        return toVo(user);
    }

    @Override
    public CurrentUserVO updateAvatar(
            MultipartFile file, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = stored();
        String next = storeAvatar(file);
        String previous = user.getAvatarUrl();
        user.setAvatarUrl(next);
        userMapper.updateProfile(user.getId(), user.getDisplayName(), next);
        if (previous != null && !previous.equals(next)) {
            deleteUpload(previous);
        }
        remember(user, httpRequest, httpResponse);
        return toVo(user);
    }

    private User stored() {
        CurrentUser current = CurrentUsers.require();
        User user = userMapper.findById(current.id());
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        return user;
    }

    private void remember(User user, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        OwnerUser owner = new OwnerUser(
                user.getId(), user.getEmail(), user.getPasswordHash(), user.getDisplayName(), user.getAvatarUrl());
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(owner, null, owner.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }

    private CurrentUserVO toVo(User user) {
        return new CurrentUserVO(user.getId(), user.getEmail(), user.getDisplayName(), user.getAvatarUrl());
    }

    private String storeAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("请选择图片");
        }
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(type)) {
            throw new BadRequestException("只接收 jpg、png、webp");
        }
        try {
            BufferedImage source = ImageIO.read(file.getInputStream());
            if (source == null) {
                throw new BadRequestException("这张图无法读取");
            }
            String filename = UUID.randomUUID() + ".jpg";
            Path directory = uploadDirectory();
            Files.createDirectories(directory);
            Thumbnails.of(source)
                    .size(AVATAR_EDGE, AVATAR_EDGE)
                    .outputFormat("jpg")
                    .outputQuality(0.9)
                    .toFile(directory.resolve(filename).toFile());
            return "/uploads/" + filename;
        } catch (IOException exception) {
            throw new BadRequestException("图片处理失败");
        }
    }

    private void deleteUpload(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        String filename = url.substring("/uploads/".length());
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return;
        }
        try {
            Files.deleteIfExists(uploadDirectory().resolve(filename));
        } catch (IOException ignored) {
            // 旧头像文件已经不在时，仍然换上新地址。
        }
    }

    private Path uploadDirectory() {
        Path path = Path.of(properties.uploadDir());
        if (!path.isAbsolute()) {
            path = Path.of(System.getProperty("user.dir")).resolve(path);
        }
        return path;
    }
}
