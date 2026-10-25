package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Post;
import com.dayandnight.dayandnightserver.vo.PlaceVO;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    List<PostSummaryVO> findPage(
            @Param("publicOnly") boolean publicOnly,
            @Param("authorId") Long authorId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countPage(@Param("publicOnly") boolean publicOnly, @Param("authorId") Long authorId);

    List<PlaceVO> findPlaced(@Param("publicOnly") boolean publicOnly);

    PostBodyVO findById(long id);

    int insert(Post post);

    int update(Post post);

    int delete(long id);
}
