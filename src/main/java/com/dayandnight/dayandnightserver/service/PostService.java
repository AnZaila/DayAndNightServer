package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreatePostDTO;
import com.dayandnight.dayandnightserver.dto.UpdatePostDTO;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import com.dayandnight.dayandnightserver.vo.PostDetailVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    List<PostSummaryVO> list();

    PostDetailVO get(long id);

    PostDetailVO create(CreatePostDTO request);

    PostDetailVO update(long id, UpdatePostDTO request);

    void delete(long id);

    List<MediaVO> addMedia(long id, List<MultipartFile> files);

    void deleteMedia(long id, long mediaId);
}
