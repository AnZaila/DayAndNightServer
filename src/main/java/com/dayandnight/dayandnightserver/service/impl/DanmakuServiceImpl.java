package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.Pages;
import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.dto.CreateDanmakuDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.enums.CommentMode;
import com.dayandnight.dayandnightserver.enums.PostStatus;
import com.dayandnight.dayandnightserver.mapper.DanmakuMapper;
import com.dayandnight.dayandnightserver.mapper.PostMapper;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.pojo.Danmaku;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.service.DanmakuService;
import com.dayandnight.dayandnightserver.vo.DanmakuVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class DanmakuServiceImpl implements DanmakuService {

    private final DanmakuMapper danmakuMapper;
    private final PostMapper postMapper;
    private final SiteSettingMapper siteSettingMapper;

    public DanmakuServiceImpl(DanmakuMapper danmakuMapper, PostMapper postMapper, SiteSettingMapper siteSettingMapper) {
        this.danmakuMapper = danmakuMapper;
        this.postMapper = postMapper;
        this.siteSettingMapper = siteSettingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<DanmakuVO> listForPost(long postId, int page, int size, String remoteAddress) {
        requireReadable(postId);
        return visiblePage(postId, page, size, remoteAddress);
    }

    @Override
    @Transactional
    public DanmakuVO createForPost(long postId, CreateDanmakuDTO request, String remoteAddress) {
        requireReadable(postId);
        return insert(postId, request, remoteAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<DanmakuVO> listMap(int page, int size, String remoteAddress) {
        return visiblePage(null, page, size, remoteAddress);
    }

    @Override
    @Transactional
    public DanmakuVO createForMap(CreateDanmakuDTO request, String remoteAddress) {
        return insert(null, request, remoteAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<DanmakuVO> recent(int page, int size, String remoteAddress) {
        CurrentUsers.require();
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 20);
        String ipHash = hash(remoteAddress);
        List<DanmakuVO> items = danmakuMapper.findRecent(ipHash, safeSize, Pages.offset(safePage, safeSize));
        return PageResult.of(items, danmakuMapper.countRecent(), safePage, safeSize);
    }

    @Override
    @Transactional
    public DanmakuVO update(long id, UpdateCommentDTO request, String remoteAddress) {
        CurrentUsers.require();
        String ipHash = hash(remoteAddress);
        if (danmakuMapper.findById(id, ipHash) == null) {
            throw new NotFoundException("弹幕不存在");
        }
        danmakuMapper.updateStatus(id, request.status().name());
        return danmakuMapper.findById(id, ipHash);
    }

    @Override
    @Transactional
    public DanmakuVO toggleLike(long id, String remoteAddress) {
        String ipHash = hash(remoteAddress);
        DanmakuVO existing = danmakuMapper.findById(id, ipHash);
        if (existing == null) {
            throw new NotFoundException("弹幕不存在");
        }
        if (existing.postId() != null) {
            requireReadable(existing.postId());
        }
        CurrentUser author = CurrentUsers.optional().orElse(null);
        if (danmakuMapper.countLike(id, ipHash) > 0) {
            danmakuMapper.deleteLike(id, ipHash);
        } else {
            danmakuMapper.insertLike(id, ipHash, author == null ? null : author.id());
        }
        return danmakuMapper.findById(id, ipHash);
    }

    @Override
    @Transactional
    public void delete(long id) {
        CurrentUsers.require();
        if (danmakuMapper.findById(id, "") == null) {
            throw new NotFoundException("弹幕不存在");
        }
        danmakuMapper.delete(id);
    }

    private PageResult<DanmakuVO> visiblePage(Long postId, int page, int size, String remoteAddress) {
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 20);
        String ipHash = hash(remoteAddress);
        List<DanmakuVO> items = danmakuMapper.findVisible(postId, ipHash, safeSize, Pages.offset(safePage, safeSize));
        return PageResult.of(items, danmakuMapper.countVisible(postId), safePage, safeSize);
    }

    private DanmakuVO insert(Long postId, CreateDanmakuDTO request, String remoteAddress) {
        String ipHash = hash(remoteAddress);
        CurrentUser author = CurrentUsers.optional().orElse(null);
        SiteSetting site = siteSettingMapper.find();
        Danmaku danmaku = new Danmaku();
        danmaku.setPostId(postId);
        danmaku.setUserId(author == null ? null : author.id());
        danmaku.setNickname(nicknameFor(request, author));
        danmaku.setContent(request.content().trim());
        danmaku.setStatus(site != null && CommentMode.REVIEW.name().equals(site.getCommentMode())
                ? "HIDDEN"
                : "VISIBLE");
        danmaku.setIpHash(ipHash);
        danmakuMapper.insert(danmaku);
        return danmakuMapper.findById(danmaku.getId(), ipHash);
    }

    private PostBodyVO requireReadable(long postId) {
        PostBodyVO post = postMapper.findById(postId);
        if (post == null) {
            throw new NotFoundException("这篇不存在");
        }
        boolean open = "PUBLIC".equals(post.visibility()) && PostStatus.PUBLISHED.name().equals(post.status());
        if (!open && CurrentUsers.optional().isEmpty()) {
            throw new NotFoundException("这篇不存在");
        }
        return post;
    }

    private String nicknameFor(CreateDanmakuDTO request, CurrentUser user) {
        if (user != null) {
            String name = user.displayName();
            return name.length() > 20 ? name.substring(0, 20) : name;
        }
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new BadRequestException("留一个昵称");
        }
        return request.nickname().trim();
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

}
