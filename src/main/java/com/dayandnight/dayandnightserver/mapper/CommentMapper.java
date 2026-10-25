package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Comment;
import com.dayandnight.dayandnightserver.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<CommentVO> findByPostId(@Param("postId") long postId, @Param("visibleOnly") boolean visibleOnly);

    List<CommentVO> findRecent();

    CommentVO findById(long id);

    int insert(Comment comment);

    int updateStatus(@Param("id") long id, @Param("status") String status);

    int delete(long id);
}
