package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.vo.MonthStatVO;
import com.dayandnight.dayandnightserver.vo.NamedCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatsMapper {

    long countPosts(@Param("publicOnly") boolean publicOnly);

    long sumViews(@Param("publicOnly") boolean publicOnly);

    long countLikes(@Param("publicOnly") boolean publicOnly);

    long countComments(@Param("publicOnly") boolean publicOnly);

    List<MonthStatVO> months(@Param("publicOnly") boolean publicOnly);

    List<NamedCountVO> moods(@Param("publicOnly") boolean publicOnly);
}
