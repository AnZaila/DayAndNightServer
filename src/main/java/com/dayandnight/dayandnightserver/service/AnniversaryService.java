package com.dayandnight.dayandnightserver.service;

import com.dayandnight.dayandnightserver.dto.UpsertAnniversaryDTO;
import com.dayandnight.dayandnightserver.vo.AnniversaryVO;

import java.util.List;

public interface AnniversaryService {

    List<AnniversaryVO> list();

    AnniversaryVO create(UpsertAnniversaryDTO request);

    AnniversaryVO update(long id, UpsertAnniversaryDTO request);

    void delete(long id);
}
