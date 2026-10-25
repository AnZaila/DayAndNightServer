package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteSettingMapper {

    SiteSetting find();

    int insert(SiteSetting setting);

    int update(SiteSetting setting);
}
