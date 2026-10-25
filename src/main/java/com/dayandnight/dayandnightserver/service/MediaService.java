package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.vo.MediaVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {

    List<MediaVO> store(long postId, List<MultipartFile> files);

    void delete(long postId, long mediaId);

    void deleteAllFiles(long postId);
}
