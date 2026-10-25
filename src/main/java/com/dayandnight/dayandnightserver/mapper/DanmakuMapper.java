package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Danmaku;
import com.dayandnight.dayandnightserver.vo.DanmakuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DanmakuMapper {

    List<DanmakuVO> findByPostId(@Param("postId") long postId, @Param("ipHash") String ipHash);

    List<DanmakuVO> findMap(@Param("ipHash") String ipHash);

    List<DanmakuVO> findRecent(@Param("ipHash") String ipHash);

    DanmakuVO findById(@Param("id") long id, @Param("ipHash") String ipHash);

    int insert(Danmaku danmaku);

    int updateStatus(@Param("id") long id, @Param("status") String status);

    int delete(long id);

    int countLike(@Param("danmakuId") long danmakuId, @Param("ipHash") String ipHash);

    int insertLike(@Param("danmakuId") long danmakuId, @Param("ipHash") String ipHash, @Param("userId") Long userId);

    int deleteLike(@Param("danmakuId") long danmakuId, @Param("ipHash") String ipHash);
}
