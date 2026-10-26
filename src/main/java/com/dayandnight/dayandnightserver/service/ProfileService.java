package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.vo.ActivityVO;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import com.dayandnight.dayandnightserver.vo.ProfileVO;

public interface ProfileService {

    ProfileVO view(long id);

    PageResult<CommentVO> comments(long id, int page, int size, String remoteAddress);

    PageResult<PostSummaryVO> posts(long id, int page, int size, String remoteAddress);

    PageResult<PostSummaryVO> likedPosts(long id, int page, int size, String remoteAddress);

    PageResult<ActivityVO> received(long id, int page, int size);
}
