package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.CreatePostDTO;
import com.dayandnight.dayandnightserver.dto.UpdatePostDTO;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import com.dayandnight.dayandnightserver.vo.PlaceVO;
import com.dayandnight.dayandnightserver.vo.PostDetailVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PageResult<PostSummaryVO> list(int page, int size, boolean mine, boolean ascending, String remoteAddress);

    PageResult<PostSummaryVO> listByAuthor(long authorId, int page, int size, String remoteAddress);

    PageResult<PostSummaryVO> likedBy(long userId, int page, int size, String remoteAddress);

    List<PlaceVO> places();

    PostDetailVO get(long id, String remoteAddress, boolean recordView);

    PostDetailVO toggleLike(long id, String remoteAddress);

    PostDetailVO togglePin(long id);

    PostDetailVO create(CreatePostDTO request);

    PostDetailVO update(long id, UpdatePostDTO request);

    void delete(long id);

    MediaVO addCover(long id, MultipartFile file);

    void deleteMedia(long id, long mediaId);
}
