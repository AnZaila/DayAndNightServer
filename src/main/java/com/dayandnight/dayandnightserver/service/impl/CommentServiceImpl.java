package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.GuestAvatars;
import com.dayandnight.dayandnightserver.common.Pages;
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
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

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
    public PageResult<CommentVO> listForPost(long postId, int page, int size, String remoteAddress) {
        PostBodyVO post = requireReadable(postId);
        boolean visibleOnly = CurrentUsers.optional().isEmpty();
        if (!isPublic(post) && visibleOnly) {
            throw new NotFoundException("这篇不存在");
        }
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 10);
        String ipHash = hash(remoteAddress);
        List<CommentVO> roots = commentMapper.findRoots(
                postId, visibleOnly, ipHash, safeSize, Pages.offset(safePage, safeSize));
        long total = commentMapper.countRoots(postId, visibleOnly);
        List<CommentVO> items = new ArrayList<>(roots);
        if (!roots.isEmpty()) {
            List<Long> ids = roots.stream().map(CommentVO::id).toList();
            items.addAll(commentMapper.findReplies(postId, ids, visibleOnly, ipHash));
        }
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional
    public CommentVO create(long postId, CreateCommentDTO request, String remoteAddress) {
        PostBodyVO post = postMapper.findById(postId, "");
        if (post == null || !isPublic(post)) {
            if (post == null || CurrentUsers.optional().isEmpty()) {
                throw new NotFoundException("这篇不存在");
            }
            throw new BadRequestException("只有公开的帖子可以评论");
        }
        if (!Boolean.TRUE.equals(post.commentsOpen())) {
            throw new BadRequestException("这篇暂时不接收评论");
        }
        String ipHash = hash(remoteAddress);
        CurrentUser author = CurrentUsers.optional().orElse(null);
        ReplyTarget reply = resolveReply(postId, request.parentId(), ipHash, author);
        if (author == null) {
            int existing = commentMapper.countVisitor(postId, ipHash, reply.parentId());
            if (existing > 0) {
                throw new BadRequestException(reply.parentId() == null
                        ? "这个地址在这篇下面只能留一条评论"
                        : "这个地址在这条下面已经回复过一次");
            }
        }
        SiteSetting site = siteSettingMapper.find();
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setParentId(reply.parentId());
        comment.setReplyToNickname(reply.replyToNickname());
        comment.setUserId(author == null ? null : author.id());
        comment.setNickname(nicknameFor(request, author));
        comment.setContent(request.content().trim());
        comment.setStatus(site != null && CommentMode.REVIEW.name().equals(site.getCommentMode())
                ? "HIDDEN"
                : "VISIBLE");
        comment.setIpHash(ipHash);
        if (author == null) {
            comment.setAvatarUrl(GuestAvatars.normalize(request.avatarUrl()));
        }
        commentMapper.insert(comment);
        return commentMapper.findById(comment.getId(), ipHash);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CommentVO> recent(String scope, int page, int size, String remoteAddress) {
        CurrentUser user = CurrentUsers.require();
        boolean mine = "mine".equals(scope);
        if (!mine && !"manage".equals(scope)) {
            throw new BadRequestException("评论范围不对");
        }
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 20);
        String ipHash = hash(remoteAddress);
        List<CommentVO> items = commentMapper.findRecent(
                user.id(), mine, ipHash, safeSize, Pages.offset(safePage, safeSize));
        long total = commentMapper.countRecent(user.id(), mine);
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CommentVO> listByAuthor(long userId, int page, int size, String remoteAddress) {
        boolean publicOnly = CurrentUsers.optional().isEmpty();
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 10);
        String ipHash = hash(remoteAddress);
        List<CommentVO> items = commentMapper.findByAuthor(
                userId, publicOnly, ipHash, safeSize, Pages.offset(safePage, safeSize));
        long total = commentMapper.countByAuthor(userId, publicOnly);
        return PageResult.of(items, total, safePage, safeSize);
    }

    @Override
    @Transactional
    public CommentVO update(long id, UpdateCommentDTO request, String remoteAddress) {
        CurrentUsers.require();
        String ipHash = hash(remoteAddress);
        if (commentMapper.findById(id, ipHash) == null) {
            throw new NotFoundException("评论不存在");
        }
        commentMapper.updateStatus(id, request.status().name());
        return commentMapper.findById(id, ipHash);
    }

    @Override
    @Transactional
    public CommentVO toggleLike(long id, String remoteAddress) {
        String ipHash = hash(remoteAddress);
        CommentVO existing = commentMapper.findById(id, ipHash);
        if (existing == null) {
            throw new NotFoundException("评论不存在");
        }
        requireReadable(existing.postId());
        CurrentUser author = CurrentUsers.optional().orElse(null);
        if (commentMapper.countLike(id, ipHash) > 0) {
            commentMapper.deleteLike(id, ipHash);
        } else {
            commentMapper.insertLike(id, ipHash, author == null ? null : author.id());
        }
        return commentMapper.findById(id, ipHash);
    }

    @Override
    @Transactional
    public void delete(long id) {
        CurrentUsers.require();
        if (commentMapper.findById(id, "") == null) {
            throw new NotFoundException("评论不存在");
        }
        commentMapper.delete(id);
    }

    private ReplyTarget resolveReply(long postId, Long targetId, String ipHash, CurrentUser author) {
        if (targetId == null) {
            return new ReplyTarget(null, null);
        }
        CommentVO target = commentMapper.findById(targetId, ipHash);
        if (target == null || !Objects.equals(target.postId(), postId)) {
            throw new BadRequestException("要回复的评论不存在");
        }
        if ("HIDDEN".equals(target.status()) && author == null) {
            throw new BadRequestException("要回复的评论不存在");
        }
        Long rootId = target.parentId() == null ? target.id() : target.parentId();
        String replyToNickname = target.parentId() == null ? null : target.nickname();
        return new ReplyTarget(rootId, replyToNickname);
    }

    private PostBodyVO requireReadable(long postId) {
        PostBodyVO post = postMapper.findById(postId, "");
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

    private String nicknameFor(CreateCommentDTO request, CurrentUser user) {
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

    private record ReplyTarget(Long parentId, String replyToNickname) {
    }
}
