package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.mapper.StatsMapper;
import com.dayandnight.dayandnightserver.service.StatsService;
import com.dayandnight.dayandnightserver.vo.SiteStatsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;

    public StatsServiceImpl(StatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SiteStatsVO view() {
        boolean publicOnly = CurrentUsers.optional().isEmpty();
        return new SiteStatsVO(
                statsMapper.countPosts(publicOnly),
                statsMapper.sumViews(publicOnly),
                statsMapper.countLikes(publicOnly),
                statsMapper.countComments(publicOnly),
                statsMapper.months(publicOnly),
                statsMapper.moods(publicOnly));
    }
}
