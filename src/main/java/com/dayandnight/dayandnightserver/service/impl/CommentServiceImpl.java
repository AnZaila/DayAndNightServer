package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.dto.CreateCommentDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.enums.CommentMode;
import com.dayandnight.dayandnightserver.enums.PostStatus;
import com.dayandnight.dayandnightserver.mapper.CommentMapper;
import com.dayandnight.dayandnightserver.mapper.PostMapper;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.pojo.Comment;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.service.CommentService;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final SiteSettingMapper siteSettingMapper;

    public CommentServiceImpl(CommentMapper commentMapper, PostMapper postMapper, SiteSettingMapper siteSettingMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
        this.siteSettingMapper = siteSettingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentVO> listForPost(long postId) {
        PostBodyVO post = requireReadable(postId);
        boolean visibleOnly = CurrentUsers.optional().isEmpty();
        if (!isPublic(post) && visibleOnly) {
            throw new NotFoundException("这篇不存在");
        }
        return commentMapper.findByPostId(postId, visibleOnly);
    }

    @Override
    @Transactional
    public CommentVO create(long postId, CreateCommentDTO request, String remoteAddress) {
        PostBodyVO post = postMapper.findById(postId);
        if (post == null || !isPublic(post)) {
            if (post == null || CurrentUsers.optional().isEmpty()) {
                throw new NotFoundException("这篇不存在");
            }
            throw new BadRequestException("只有公开的帖子可以评论");
        }
        if (!Boolean.TRUE.equals(post.commentsOpen())) {
            throw new BadRequestException("这篇暂时不接收评论");
        }
        SiteSetting site = siteSettingMapper.find();
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setNickname(nicknameFor(request));
        comment.setContent(request.content().trim());
        comment.setStatus(site != null && CommentMode.REVIEW.name().equals(site.getCommentMode())
                ? "HIDDEN"
                : "VISIBLE");
        comment.setIpHash(hash(remoteAddress));
        commentMapper.insert(comment);
        return commentMapper.findById(comment.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentVO> recent() {
        CurrentUsers.require();
        return commentMapper.findRecent();
    }

    @Override
    @Transactional
    public CommentVO update(long id, UpdateCommentDTO request) {
        CurrentUsers.require();
        if (commentMapper.findById(id) == null) {
            throw new NotFoundException("评论不存在");
        }
        commentMapper.updateStatus(id, request.status().name());
        return commentMapper.findById(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        CurrentUsers.require();
        if (commentMapper.findById(id) == null) {
            throw new NotFoundException("评论不存在");
        }
        commentMapper.delete(id);
    }

    private PostBodyVO requireReadable(long postId) {
        PostBodyVO post = postMapper.findById(postId);
        if (post == null) {
            throw new NotFoundException("这篇不存在");
        }
        if (!isPublic(post) && CurrentUsers.optional().isEmpty()) {
            throw new NotFoundException("这篇不存在");
        }
        return post;
    }

    private boolean isPublic(PostBodyVO post) {
        return "PUBLIC".equals(post.visibility()) && PostStatus.PUBLISHED.name().equals(post.status());
    }

    private String nicknameFor(CreateCommentDTO request) {
        CurrentUser user = CurrentUsers.optional().orElse(null);
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
