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
            @Param("ipHash") String ipHash,
            @Param("ascending") boolean ascending,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countPage(@Param("publicOnly") boolean publicOnly, @Param("authorId") Long authorId);

    List<PlaceVO> findPlaced(@Param("publicOnly") boolean publicOnly);

    PostBodyVO findById(@Param("id") long id, @Param("ipHash") String ipHash);

    int insert(Post post);

    int update(Post post);

    int delete(long id);

    int insertView(@Param("postId") long postId, @Param("ipHash") String ipHash);

    int incrementViewCount(long id);

    int countLike(@Param("postId") long postId, @Param("ipHash") String ipHash);

    int insertLike(@Param("postId") long postId, @Param("ipHash") String ipHash, @Param("userId") Long userId);

    int deleteLike(@Param("postId") long postId, @Param("ipHash") String ipHash);

    int togglePinned(@Param("id") long id);

    List<PostSummaryVO> findLikedByUser(
            @Param("userId") long userId,
            @Param("ipHash") String ipHash,
            @Param("publicOnly") boolean publicOnly,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countLikedByUser(@Param("userId") long userId, @Param("publicOnly") boolean publicOnly);
}
