package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreateDanmakuDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.vo.DanmakuVO;

import java.util.List;

public interface DanmakuService {

    List<DanmakuVO> listForPost(long postId, String remoteAddress);

    DanmakuVO createForPost(long postId, CreateDanmakuDTO request, String remoteAddress);

    List<DanmakuVO> listMap(String remoteAddress);

    DanmakuVO createForMap(CreateDanmakuDTO request, String remoteAddress);

    List<DanmakuVO> recent(String remoteAddress);

    DanmakuVO update(long id, UpdateCommentDTO request, String remoteAddress);

    DanmakuVO toggleLike(long id, String remoteAddress);

    void delete(long id);
}
