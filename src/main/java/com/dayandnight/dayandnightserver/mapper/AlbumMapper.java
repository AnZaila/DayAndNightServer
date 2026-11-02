package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.AlbumPhoto;
import com.dayandnight.dayandnightserver.vo.AlbumPhotoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumMapper {

    List<AlbumPhotoVO> findPage(
            @Param("publicOnly") boolean publicOnly, @Param("limit") int limit, @Param("offset") int offset);

    long countPage(@Param("publicOnly") boolean publicOnly);

    int insert(AlbumPhoto photo);

    AlbumPhoto findById(long id);

    int delete(long id);
}
