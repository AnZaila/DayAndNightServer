package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.Pages;
import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.ForbiddenException;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.dto.CreatePostDTO;
import com.dayandnight.dayandnightserver.dto.UpdatePostDTO;
import com.dayandnight.dayandnightserver.enums.PostStatus;
import com.dayandnight.dayandnightserver.mapper.PostMapper;
import com.dayandnight.dayandnightserver.mapper.PostMediaMapper;
import com.dayandnight.dayandnightserver.pojo.Post;
import com.dayandnight.dayandnightserver.service.MediaService;
import com.dayandnight.dayandnightserver.service.PostService;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PlaceVO;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import com.dayandnight.dayandnightserver.vo.PostDetailVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostMediaMapper postMediaMapper;
    private final MediaService mediaService;

    public PostServiceImpl(PostMapper postMapper, PostMediaMapper postMediaMapper, MediaService mediaService) {
        this.postMapper = postMapper;
        this.postMediaMapper = postMediaMapper;
        this.mediaService = mediaService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PostSummaryVO> list(int page, int size, boolean mine, boolean ascending, String remoteAddress) {
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 10);
        boolean signedIn = CurrentUsers.optional().isPresent();
        boolean publicOnly = !signedIn;
        Long authorId = signedIn && mine ? CurrentUsers.require().id() : null;
        String ipHash = viewerKey(remoteAddress);
        List<PostSummaryVO> items = postMapper.findPage(
                publicOnly, authorId, ipHash, ascending, safeSize, Pages.offset(safePage, safeSize));
        long total = postMapper.countPage(publicOnly, authorId);
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PostSummaryVO> listByAuthor(long authorId, int page, int size, String remoteAddress) {
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 10);
        boolean publicOnly = CurrentUsers.optional().isEmpty();
        String ipHash = viewerKey(remoteAddress);
        List<PostSummaryVO> items = postMapper.findPage(
                publicOnly, authorId, ipHash, false, safeSize, Pages.offset(safePage, safeSize));
        long total = postMapper.countPage(publicOnly, authorId);
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PostSummaryVO> likedBy(long userId, int page, int size, boolean publicOnly, String remoteAddress) {
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 10);
        String ipHash = viewerKey(remoteAddress);
        List<PostSummaryVO> items = postMapper.findLikedByUser(
                userId, ipHash, publicOnly, safeSize, Pages.offset(safePage, safeSize));
        long total = postMapper.countLikedByUser(userId, publicOnly);
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceVO> places() {
        return postMapper.findPlaced(CurrentUsers.optional().isEmpty());
    }

    @Override
    @Transactional
    public PostDetailVO get(long id, String remoteAddress, boolean recordView) {
        String ipHash = viewerKey(remoteAddress);
        PostBodyVO body = requireVisible(id, ipHash);
        if (recordView) {
            postMapper.incrementViewCount(id);
            body = postMapper.findById(id, ipHash);
        }
        return assemble(body);
    }

    @Override
    @Transactional
    public PostDetailVO toggleLike(long id, String remoteAddress) {
        String ipHash = viewerKey(remoteAddress);
        requireVisible(id, ipHash);
        CurrentUser author = CurrentUsers.optional().orElse(null);
        if (postMapper.countLike(id, ipHash) > 0) {
            postMapper.deleteLike(id, ipHash);
        } else {
            postMapper.insertLike(id, ipHash, author == null ? null : author.id());
        }
        return assemble(postMapper.findById(id, ipHash));
    }

    @Override
    @Transactional
    public PostDetailVO togglePin(long id) {
        requireVisible(id, "");
        CurrentUsers.require();
        postMapper.togglePinned(id);
        return assemble(postMapper.findById(id, viewerKey("")));
    }

    @Override
    @Transactional
    public PostDetailVO create(CreatePostDTO request) {
        CurrentUser author = CurrentUsers.require();
        Post post = Post.forInsert(author.id(), request);
        applyCoordinates(post, request.latitude(), request.longitude());
        postMapper.insert(post);
        return assemble(postMapper.findById(post.getId(), ""));
    }

    @Override
    @Transactional
    public PostDetailVO update(long id, UpdatePostDTO request) {
        PostBodyVO existing = requireVisible(id, "");
        requireAuthor(existing);
        Post post = new Post();
        post.setId(id);
        post.setContent(request.content().trim());
        post.setHappenedAt(request.happenedAt());
        post.setMood(blankToNull(request.mood()));
        post.setWeather(blankToNull(request.weather()));
        post.setLocation(blankToNull(request.location()));
        post.setVisibility(request.visibility().name());
        post.setStatus(request.status().name());
        post.setCommentsOpen(request.commentsOpen());
        applyCoordinates(post, request.latitude(), request.longitude());
        postMapper.update(post);
        return assemble(postMapper.findById(id, ""));
    }

    @Override
    @Transactional
    public void delete(long id) {
        PostBodyVO existing = requireVisible(id, "");
        requireAuthor(existing);
        mediaService.deleteAllFiles(id);
        postMapper.delete(id);
    }

    @Override
    @Transactional
    public MediaVO addCover(long id, MultipartFile file) {
        requireAuthor(requireVisible(id, ""));
        return mediaService.storeCover(id, file);
    }

    @Override
    @Transactional
    public void deleteMedia(long id, long mediaId) {
        requireAuthor(requireVisible(id, ""));
        mediaService.delete(id, mediaId);
    }

    private PostDetailVO assemble(PostBodyVO body) {
        return new PostDetailVO(
                body.id(),
                body.authorId(),
                body.displayName(),
                body.content(),
                body.happenedAt(),
                body.mood(),
                body.weather(),
                body.location(),
                body.visibility(),
                body.status(),
                body.commentsOpen(),
                postMediaMapper.findByPostId(body.id()),
                body.authorAvatarUrl(),
                body.latitude(),
                body.longitude(),
                body.viewCount(),
                body.likeCount(),
                body.commentCount(),
                body.liked(),
                Boolean.TRUE.equals(body.pinned()));
    }

    private PostBodyVO requireVisible(long id, String ipHash) {
        PostBodyVO body = postMapper.findById(id, ipHash);
        if (body == null) {
            throw new NotFoundException("这篇不存在");
        }
        boolean owner = CurrentUsers.optional().isPresent();
        boolean open = "PUBLIC".equals(body.visibility()) && PostStatus.PUBLISHED.name().equals(body.status());
        if (!owner && !open) {
            throw new NotFoundException("这篇不存在");
        }
        return body;
    }

    private void requireAuthor(PostBodyVO body) {
        CurrentUser user = CurrentUsers.require();
        if (!user.id().equals(body.authorId())) {
            throw new ForbiddenException("只能修改自己的帖子");
        }
    }

    private static void applyCoordinates(Post post, BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null && longitude == null) {
            post.setLatitude(null);
            post.setLongitude(null);
            return;
        }
        if (latitude == null || longitude == null) {
            throw new BadRequestException("地点需要同时标记纬度和经度");
        }
        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("纬度超出范围");
        }
        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("经度超出范围");
        }
        post.setLatitude(latitude.setScale(6, RoundingMode.HALF_UP));
        post.setLongitude(longitude.setScale(6, RoundingMode.HALF_UP));
    }

    private String viewerKey(String remoteAddress) {
        return CurrentUsers.optional()
                .map(user -> hash("user:" + user.id()))
                .orElseGet(() -> hash(remoteAddress == null ? "unknown" : remoteAddress));
    }

    private String hash(String remoteAddress) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(("dayandnight:" + remoteAddress).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
