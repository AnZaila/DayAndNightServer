package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreateCommentDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.vo.CommentVO;

import java.util.List;

public interface CommentService {

    List<CommentVO> listForPost(long postId);

    CommentVO create(long postId, CreateCommentDTO request, String remoteAddress);

    List<CommentVO> recent();

    CommentVO update(long id, UpdateCommentDTO request);

    void delete(long id);
}
