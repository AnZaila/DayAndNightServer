package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.enums.ProfileVisibility;
import com.dayandnight.dayandnightserver.mapper.UserMapper;
import com.dayandnight.dayandnightserver.pojo.User;
import com.dayandnight.dayandnightserver.service.CommentService;
import com.dayandnight.dayandnightserver.service.PostService;
import com.dayandnight.dayandnightserver.service.ProfileService;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import com.dayandnight.dayandnightserver.vo.ProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final CommentService commentService;
    private final PostService postService;

    public ProfileServiceImpl(UserMapper userMapper, CommentService commentService, PostService postService) {
        this.userMapper = userMapper;
        this.commentService = commentService;
        this.postService = postService;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileVO view(long id) {
        return present(requireUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CommentVO> comments(long id, int page, int size, String remoteAddress) {
        User user = requireUser(id);
        ProfileVO profile = present(user);
        if (!profile.open() || !profile.showComments()) {
            throw new NotFoundException("这份主页收起来了");
        }
        return commentService.listByAuthor(id, page, size, remoteAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PostSummaryVO> posts(long id, int page, int size) {
        User user = requireUser(id);
        if (!present(user).open()) {
            throw new NotFoundException("这份主页收起来了");
        }
        return postService.listByAuthor(id, page, size);
    }

    private User requireUser(long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new NotFoundException("这份主页不存在");
        }
        return user;
    }

    private ProfileVO present(User user) {
        boolean self = CurrentUsers.optional().map(current -> current.id().equals(user.getId())).orElse(false);
        boolean signedIn = CurrentUsers.optional().isPresent();
        ProfileVisibility visibility = visibilityOf(user.getProfileVisibility());
        boolean open = switch (visibility) {
            case PUBLIC -> true;
            case PRIVATE -> signedIn;
            case SELF -> self;
        };
        return new ProfileVO(
                user.getId(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                open && user.isShowCover() ? user.getCoverUrl() : null,
                open && user.isShowSignature() ? user.getSignature() : null,
                open && user.isShowEmail() ? user.getEmail() : null,
                open,
                self,
                open && user.isShowComments(),
                self ? visibility.name() : null);
    }

    private static ProfileVisibility visibilityOf(String value) {
        try {
            return ProfileVisibility.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return ProfileVisibility.PUBLIC;
        }
    }
}
