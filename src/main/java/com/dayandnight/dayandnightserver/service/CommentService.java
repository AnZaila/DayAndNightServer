package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreateCommentDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import com.dayandnight.dayandnightserver.vo.PageResult;

public interface CommentService {

    PageResult<CommentVO> listForPost(long postId, int page, int size, String remoteAddress);

    CommentVO create(long postId, CreateCommentDTO request, String remoteAddress);

    PageResult<CommentVO> recent(String scope, int page, int size, String remoteAddress);

    PageResult<CommentVO> listByAuthor(long userId, int page, int size, String remoteAddress);

    CommentVO update(long id, UpdateCommentDTO request, String remoteAddress);

    CommentVO toggleLike(long id, String remoteAddress);

    void delete(long id);
}
