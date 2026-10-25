package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Comment;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<CommentVO> findRoots(
            @Param("postId") long postId,
            @Param("visibleOnly") boolean visibleOnly,
            @Param("ipHash") String ipHash,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countRoots(@Param("postId") long postId, @Param("visibleOnly") boolean visibleOnly);

    List<CommentVO> findReplies(
            @Param("postId") long postId,
            @Param("rootIds") List<Long> rootIds,
            @Param("visibleOnly") boolean visibleOnly,
            @Param("ipHash") String ipHash);

    List<CommentVO> findRecent(
            @Param("userId") long userId,
            @Param("mine") boolean mine,
            @Param("ipHash") String ipHash,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countRecent(@Param("userId") long userId, @Param("mine") boolean mine);

    CommentVO findById(@Param("id") long id, @Param("ipHash") String ipHash);

    int countVisitor(@Param("postId") long postId, @Param("ipHash") String ipHash, @Param("parentId") Long parentId);

    int insert(Comment comment);

    int updateStatus(@Param("id") long id, @Param("status") String status);

    int delete(long id);

    int countLike(@Param("commentId") long commentId, @Param("ipHash") String ipHash);

    int insertLike(@Param("commentId") long commentId, @Param("ipHash") String ipHash, @Param("userId") Long userId);

    int deleteLike(@Param("commentId") long commentId, @Param("ipHash") String ipHash);
}
