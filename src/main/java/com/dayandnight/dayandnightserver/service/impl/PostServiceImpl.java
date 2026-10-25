package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
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
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import com.dayandnight.dayandnightserver.vo.PostDetailVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public List<PostSummaryVO> list() {
        if (CurrentUsers.optional().isPresent()) {
            return postMapper.findAll();
        }
        return postMapper.findPublic();
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailVO get(long id) {
        return assemble(requireVisible(id));
    }

    @Override
    @Transactional
    public PostDetailVO create(CreatePostDTO request) {
        CurrentUser author = CurrentUsers.require();
        Post post = Post.forInsert(author.id(), request);
        postMapper.insert(post);
        return get(post.getId());
    }

    @Override
    @Transactional
    public PostDetailVO update(long id, UpdatePostDTO request) {
        PostBodyVO existing = requireVisible(id);
        requireAuthor(existing);
        Post post = new Post();
        post.setId(id);
        post.setContent(request.content().trim());
        post.setHappenedAt(request.happenedAt());
        post.setMood(blankToNull(request.mood()));
        post.setLocation(blankToNull(request.location()));
        post.setVisibility(request.visibility().name());
        post.setStatus(request.status().name());
        post.setCommentsOpen(request.commentsOpen());
        postMapper.update(post);
        return get(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        PostBodyVO existing = requireVisible(id);
        requireAuthor(existing);
        mediaService.deleteAllFiles(id);
        postMapper.delete(id);
    }

    @Override
    @Transactional
    public List<MediaVO> addMedia(long id, List<MultipartFile> files) {
        requireAuthor(requireVisible(id));
        return mediaService.store(id, files);
    }

    @Override
    @Transactional
    public void deleteMedia(long id, long mediaId) {
        requireAuthor(requireVisible(id));
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
                body.location(),
                body.visibility(),
                body.status(),
                body.commentsOpen(),
                postMediaMapper.findByPostId(body.id()));
    }

    private PostBodyVO requireVisible(long id) {
        PostBodyVO body = postMapper.findById(id);
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

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
