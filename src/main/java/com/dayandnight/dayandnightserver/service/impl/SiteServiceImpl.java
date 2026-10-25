package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.dto.UpdateSiteDTO;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.service.SiteService;
import com.dayandnight.dayandnightserver.vo.SiteVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteSettingMapper siteSettingMapper;

    public SiteServiceImpl(SiteSettingMapper siteSettingMapper) {
        this.siteSettingMapper = siteSettingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SiteVO current() {
        return toVo(requireSetting());
    }

    @Override
    @Transactional
    public SiteVO update(UpdateSiteDTO request) {
        SiteSetting setting = requireSetting();
        setting.setTitle(request.title().trim());
        setting.setSubtitle(request.subtitle().trim());
        setting.setTogetherSince(request.togetherSince());
        setting.setAbout(request.about().trim());
        setting.setCommentMode(request.commentMode().name());
        siteSettingMapper.update(setting);
        return toVo(setting);
    }

    private SiteSetting requireSetting() {
        SiteSetting setting = siteSettingMapper.find();
        if (setting == null) {
            throw new IllegalStateException("站点设置还没有初始化");
        }
        return setting;
    }

    private SiteVO toVo(SiteSetting setting) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        long days = 0;
        if (!setting.getTogetherSince().isAfter(today)) {
            days = ChronoUnit.DAYS.between(setting.getTogetherSince(), today) + 1;
        }
        return new SiteVO(
                setting.getTitle(),
                setting.getSubtitle(),
                setting.getTogetherSince(),
                days,
                setting.getAbout(),
                setting.getCommentMode());
    }
}
