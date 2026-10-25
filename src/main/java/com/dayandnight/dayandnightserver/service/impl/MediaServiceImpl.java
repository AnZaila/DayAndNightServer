package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.config.DayAndNightProperties;
import com.dayandnight.dayandnightserver.mapper.PostMediaMapper;
import com.dayandnight.dayandnightserver.pojo.PostMedia;
import com.dayandnight.dayandnightserver.service.MediaService;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    private static final int MAX_IMAGES = 9;
    private static final int MAX_EDGE = 2000;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final PostMediaMapper postMediaMapper;
    private final DayAndNightProperties properties;

    public MediaServiceImpl(PostMediaMapper postMediaMapper, DayAndNightProperties properties) {
        this.postMediaMapper = postMediaMapper;
        this.properties = properties;
    }

    @Override
    public List<MediaVO> store(long postId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("请选择图片");
        }
        int existing = postMediaMapper.countByPostId(postId);
        if (existing + files.size() > MAX_IMAGES) {
            throw new BadRequestException("一篇最多 9 张图片");
        }
        List<MediaVO> stored = new ArrayList<>();
        int sort = postMediaMapper.nextSort(postId);
        for (MultipartFile file : files) {
            stored.add(storeOne(postId, file, sort));
            sort += 1;
        }
        return stored;
    }

    @Override
    public void delete(long postId, long mediaId) {
        PostMedia media = postMediaMapper.findOwned(mediaId, postId);
        if (media == null) {
            throw new NotFoundException("图片不存在");
        }
        deleteFile(media.getUrl());
        postMediaMapper.delete(mediaId);
    }

    @Override
    public void deleteAllFiles(long postId) {
        for (MediaVO media : postMediaMapper.findByPostId(postId)) {
            deleteFile(media.url());
        }
    }

    private MediaVO storeOne(long postId, MultipartFile file, int sort) {
        if (file.isEmpty()) {
            throw new BadRequestException("有一张图片是空的");
        }
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(type)) {
            throw new BadRequestException("只接收 jpg、png、webp");
        }
        try {
            BufferedImage source = ImageIO.read(file.getInputStream());
            if (source == null) {
                throw new BadRequestException("这张图无法读取");
            }
            int edge = Math.max(source.getWidth(), source.getHeight());
            double scale = edge > MAX_EDGE ? (double) MAX_EDGE / edge : 1.0;
            String filename = UUID.randomUUID() + ".jpg";
            Path directory = uploadDirectory();
            Files.createDirectories(directory);
            Path target = directory.resolve(filename);
            Thumbnails.of(source)
                    .scale(scale)
                    .outputFormat("jpg")
                    .outputQuality(0.86)
                    .toFile(target.toFile());
            BufferedImage written = ImageIO.read(target.toFile());
            PostMedia media = new PostMedia();
            media.setPostId(postId);
            media.setUrl("/uploads/" + filename);
            media.setSortOrder(sort);
            media.setWidth(written == null ? null : written.getWidth());
            media.setHeight(written == null ? null : written.getHeight());
            postMediaMapper.insert(media);
            return new MediaVO(media.getId(), media.getUrl(), media.getWidth(), media.getHeight());
        } catch (IOException exception) {
            throw new BadRequestException("图片处理失败");
        }
    }

    private void deleteFile(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        String filename = url.substring("/uploads/".length());
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return;
        }
        try {
            Files.deleteIfExists(uploadDirectory().resolve(filename));
        } catch (IOException ignored) {
            // 文件已经不在磁盘上时，仍然删掉数据库记录。
        }
    }

    private Path uploadDirectory() {
        Path path = Path.of(properties.uploadDir());
        if (!path.isAbsolute()) {
            path = Path.of(System.getProperty("user.dir")).resolve(path);
        }
        return path;
    }
}
