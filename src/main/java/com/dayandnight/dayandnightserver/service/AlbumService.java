package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.vo.AlbumPhotoVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

public interface AlbumService {

    PageResult<AlbumPhotoVO> list(int page, int size);

    AlbumPhotoVO upload(MultipartFile file);

    void delete(long id);
}
