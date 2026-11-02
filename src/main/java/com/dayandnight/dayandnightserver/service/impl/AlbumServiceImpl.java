package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.DiaryImages;
import com.dayandnight.dayandnightserver.common.Pages;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.mapper.AlbumMapper;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.pojo.AlbumPhoto;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.service.AlbumService;
import com.dayandnight.dayandnightserver.vo.AlbumPhotoVO;
import com.dayandnight.dayandnightserver.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final SiteSettingMapper siteSettingMapper;
    private final DiaryImages diaryImages;

    public AlbumServiceImpl(
            AlbumMapper albumMapper, SiteSettingMapper siteSettingMapper, DiaryImages diaryImages) {
        this.albumMapper = albumMapper;
        this.siteSettingMapper = siteSettingMapper;
        this.diaryImages = diaryImages;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AlbumPhotoVO> list(int page, int size) {
        boolean publicOnly = CurrentUsers.optional().isEmpty();
        int safePage = Pages.page(page);
        int safeSize = Pages.size(size, 24);
        return PageResult.of(
                albumMapper.findPage(publicOnly, safeSize, Pages.offset(safePage, safeSize)),
                albumMapper.countPage(publicOnly),
                safePage,
                safeSize);
    }

    @Override
    @Transactional
    public AlbumPhotoVO upload(MultipartFile file) {
        CurrentUser user = CurrentUsers.require();
        DiaryImages.Stored stored = diaryImages.store(file, watermarkText(user));
        try {
            AlbumPhoto photo = new AlbumPhoto();
            photo.setUserId(user.id());
            photo.setUrl(stored.url());
            photo.setWidth(stored.width());
            photo.setHeight(stored.height());
            albumMapper.insert(photo);
            AlbumPhoto saved = albumMapper.findById(photo.getId());
            return new AlbumPhotoVO(
                    saved.getId(),
                    "ALBUM",
                    saved.getUrl(),
                    saved.getWidth(),
                    saved.getHeight(),
                    null,
                    saved.getCreatedAt());
        } catch (RuntimeException exception) {
            diaryImages.delete(stored.url());
            throw exception;
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        CurrentUsers.require();
        AlbumPhoto photo = albumMapper.findById(id);
        if (photo == null) {
            throw new NotFoundException("这张照片不在相册里");
        }
        diaryImages.delete(photo.getUrl());
        albumMapper.delete(id);
    }

    private String watermarkText(CurrentUser user) {
        String name = user.displayName() == null || user.displayName().isBlank() ? "朝暮" : user.displayName().trim();
        SiteSetting site = siteSettingMapper.find();
        String title = site == null || site.getTitle() == null || site.getTitle().isBlank() ? "朝暮" : site.getTitle().trim();
        return "@" + name + " · " + title;
    }
}
