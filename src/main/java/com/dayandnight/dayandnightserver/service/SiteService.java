package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.UpdateSiteDTO;
import com.dayandnight.dayandnightserver.vo.SiteVO;

public interface SiteService {

    SiteVO current();

    SiteVO update(UpdateSiteDTO request);
}
