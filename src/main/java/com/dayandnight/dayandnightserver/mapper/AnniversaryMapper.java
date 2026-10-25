package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.Anniversary;
import com.dayandnight.dayandnightserver.vo.AnniversaryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnniversaryMapper {

    List<AnniversaryVO> findPublic();

    List<AnniversaryVO> findAll();

    AnniversaryVO findById(long id);

    int insert(Anniversary anniversary);

    int update(Anniversary anniversary);

    int delete(long id);
}
