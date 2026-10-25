package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.PostMedia;
import com.dayandnight.dayandnightserver.vo.MediaVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMediaMapper {

    List<MediaVO> findByPostId(long postId);

    int countByPostId(long postId);

    int nextSort(long postId);

    int insert(PostMedia media);

    PostMedia findOwned(@Param("id") long id, @Param("postId") long postId);

    int delete(long id);
}
