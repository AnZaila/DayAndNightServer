package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.CurrentUsers;
import com.dayandnight.dayandnightserver.common.exception.NotFoundException;
import com.dayandnight.dayandnightserver.dto.UpsertAnniversaryDTO;
import com.dayandnight.dayandnightserver.mapper.AnniversaryMapper;
import com.dayandnight.dayandnightserver.pojo.Anniversary;
import com.dayandnight.dayandnightserver.service.AnniversaryService;
import com.dayandnight.dayandnightserver.vo.AnniversaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnniversaryServiceImpl implements AnniversaryService {

    private final AnniversaryMapper anniversaryMapper;

    public AnniversaryServiceImpl(AnniversaryMapper anniversaryMapper) {
        this.anniversaryMapper = anniversaryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnniversaryVO> list() {
        if (CurrentUsers.optional().isPresent()) {
            return anniversaryMapper.findAll();
        }
        return anniversaryMapper.findPublic();
    }

    @Override
    @Transactional
    public AnniversaryVO create(UpsertAnniversaryDTO request) {
        CurrentUsers.require();
        Anniversary anniversary = fill(new Anniversary(), request);
        anniversaryMapper.insert(anniversary);
        return anniversaryMapper.findById(anniversary.getId());
    }

    @Override
    @Transactional
    public AnniversaryVO update(long id, UpsertAnniversaryDTO request) {
        CurrentUsers.require();
        if (anniversaryMapper.findById(id) == null) {
            throw new NotFoundException("这个日子不存在");
        }
        Anniversary anniversary = fill(new Anniversary(), request);
        anniversary.setId(id);
        anniversaryMapper.update(anniversary);
        return anniversaryMapper.findById(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        CurrentUsers.require();
        if (anniversaryMapper.findById(id) == null) {
            throw new NotFoundException("这个日子不存在");
        }
        anniversaryMapper.delete(id);
    }

    private Anniversary fill(Anniversary anniversary, UpsertAnniversaryDTO request) {
        anniversary.setTitle(request.title().trim());
        anniversary.setDate(request.date());
        anniversary.setRepeatYearly(request.repeatYearly());
        anniversary.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        anniversary.setVisibility(request.visibility().name());
        return anniversary;
    }
}
