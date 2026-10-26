package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUser;
import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.DiaryImages;
import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.mapper.PostMediaMapper;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.pojo.PostMedia;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.service.MediaService;
import com.dayandnight.dayandnightserver.vo.ImageVO;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {

    private final PostMediaMapper postMediaMapper;
    private final SiteSettingMapper siteSettingMapper;
    private final DiaryImages diaryImages;

    public MediaServiceImpl(
            PostMediaMapper postMediaMapper, SiteSettingMapper siteSettingMapper, DiaryImages diaryImages) {
        this.postMediaMapper = postMediaMapper;
        this.siteSettingMapper = siteSettingMapper;
        this.diaryImages = diaryImages;
    }

    @Override
    public MediaVO storeCover(long postId, MultipartFile file) {
        DiaryImages.Stored stored = diaryImages.store(file, watermarkText());
        try {
            PostMedia media = new PostMedia();
            media.setPostId(postId);
            media.setUrl(stored.url());
            media.setSortOrder(0);
            media.setWidth(stored.width());
            media.setHeight(stored.height());
            postMediaMapper.insert(media);
            List<String> previous = new ArrayList<>();
            for (MediaVO item : postMediaMapper.findByPostId(postId)) {
                if (!item.id().equals(media.getId())) {
                    previous.add(item.url());
                    postMediaMapper.delete(item.id());
                }
            }
            for (String url : previous) {
                diaryImages.delete(url);
            }
            return new MediaVO(media.getId(), media.getUrl(), media.getWidth(), media.getHeight());
        } catch (RuntimeException exception) {
            diaryImages.delete(stored.url());
            throw exception;
        }
    }

    @Override
    public ImageVO storeContent(MultipartFile file) {
        DiaryImages.Stored stored = diaryImages.store(file, watermarkText());
        return new ImageVO(stored.url());
    }

    @Override
    public ImageVO storeGuestAvatar(MultipartFile file) {
        DiaryImages.Stored stored = diaryImages.storeAvatar(file);
        return new ImageVO(stored.url());
    }

    @Override
    public void delete(long postId, long mediaId) {
        PostMedia media = postMediaMapper.findOwned(mediaId, postId);
        if (media == null) {
            throw new NotFoundException("图片不存在");
        }
        diaryImages.delete(media.getUrl());
        postMediaMapper.delete(mediaId);
    }

    @Override
    public void deleteAllFiles(long postId) {
        for (MediaVO media : postMediaMapper.findByPostId(postId)) {
            diaryImages.delete(media.url());
        }
    }

    private String watermarkText() {
        CurrentUser user = CurrentUsers.require();
        String name = user.displayName() == null || user.displayName().isBlank() ? "朝暮" : user.displayName().trim();
        SiteSetting site = siteSettingMapper.find();
        String title = site == null || site.getTitle() == null || site.getTitle().isBlank() ? "朝暮" : site.getTitle().trim();
        return "@" + name + " · " + title;
    }
}
