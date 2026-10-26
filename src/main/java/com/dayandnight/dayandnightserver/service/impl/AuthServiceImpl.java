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
    private static final int COVER_WIDTH = 1600;
    private static final int COVER_HEIGHT = 700;
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
            OwnerUser owner = (OwnerUser) authentication.getPrincipal();
            Authentication kept = kept(owner);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(kept);
            SecurityContextHolder.setContext(context);
            new ChangeSessionIdAuthenticationStrategy().onAuthentication(kept, httpRequest, httpResponse);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);
            User user = userMapper.findById(owner.getId());
            if (user == null) {
                throw new UnauthorizedException("请先登录");
            }
            return toVo(user);
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
        user.setSignature(blankToNull(request.signature()));
        user.setProfileVisibility(request.profileVisibility().name());
        user.setShowSignature(Boolean.TRUE.equals(request.showSignature()));
        user.setShowCover(Boolean.TRUE.equals(request.showCover()));
        user.setShowEmail(Boolean.TRUE.equals(request.showEmail()));
        user.setShowComments(Boolean.TRUE.equals(request.showComments()));
        save(user);
        remember(user, httpRequest, httpResponse);
        return toVo(user);
    }

    @Override
    public CurrentUserVO updateAvatar(
            MultipartFile file, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = stored();
        String next = storeImage(file, AVATAR_EDGE, AVATAR_EDGE);
        String previous = user.getAvatarUrl();
        user.setAvatarUrl(next);
        save(user);
        if (previous != null && !previous.equals(next)) {
            deleteUpload(previous);
        }
        remember(user, httpRequest, httpResponse);
        return toVo(user);
    }

    @Override
    public CurrentUserVO updateCover(
            MultipartFile file, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = stored();
        String next = storeImage(file, COVER_WIDTH, COVER_HEIGHT);
        String previous = user.getCoverUrl();
        user.setCoverUrl(next);
        save(user);
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
        Authentication authentication = kept(new OwnerUser(
                user.getId(), user.getEmail(), null, user.getDisplayName(), user.getAvatarUrl()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }

    private Authentication kept(OwnerUser owner) {
        OwnerUser principal = owner.withoutSecret();
        return UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
    }

    private void save(User user) {
        userMapper.updateProfile(user);
    }

    private CurrentUserVO toVo(User user) {
        return new CurrentUserVO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getCoverUrl(),
                user.getSignature(),
                user.getProfileVisibility(),
                user.isShowSignature(),
                user.isShowCover(),
                user.isShowEmail(),
                user.isShowComments());
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String storeImage(MultipartFile file, int width, int height) {
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
                    .size(width, height)
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
