package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.vo.ImageVO;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    MediaVO storeCover(long postId, MultipartFile file);

    ImageVO storeContent(MultipartFile file);

    ImageVO storeGuestAvatar(MultipartFile file);

    void delete(long postId, long mediaId);

    void deleteAllFiles(long postId);
}
