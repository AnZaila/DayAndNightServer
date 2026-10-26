package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.vo.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityMapper {

    List<ActivityVO> findReceived(
            @Param("userId") long userId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countReceived(@Param("userId") long userId);
}
