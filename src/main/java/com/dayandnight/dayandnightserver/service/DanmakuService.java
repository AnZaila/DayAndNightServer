package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreateDanmakuDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.vo.DanmakuVO;
import com.dayandnight.dayandnightserver.vo.PageResult;

public interface DanmakuService {

    PageResult<DanmakuVO> listForPost(long postId, int page, int size, String remoteAddress);

    DanmakuVO createForPost(long postId, CreateDanmakuDTO request, String remoteAddress);

    PageResult<DanmakuVO> listMap(int page, int size, String remoteAddress);

    DanmakuVO createForMap(CreateDanmakuDTO request, String remoteAddress);

    PageResult<DanmakuVO> recent(int page, int size, String remoteAddress);

    DanmakuVO update(long id, UpdateCommentDTO request, String remoteAddress);

    DanmakuVO toggleLike(long id, String remoteAddress);

    void delete(long id);
}
