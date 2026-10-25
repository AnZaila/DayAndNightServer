package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Post;
import com.dayandnight.dayandnightserver.vo.PostBodyVO;
import com.dayandnight.dayandnightserver.vo.PostSummaryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    List<PostSummaryVO> findPublic();

    List<PostSummaryVO> findAll();

    PostBodyVO findById(long id);

    int insert(Post post);

    int update(Post post);

    int delete(long id);
}
