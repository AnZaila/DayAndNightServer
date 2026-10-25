package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.common.ClientAddresses;
import com.dayandnight.dayandnightserver.dto.CreateDanmakuDTO;
import com.dayandnight.dayandnightserver.dto.UpdateCommentDTO;
import com.dayandnight.dayandnightserver.service.DanmakuService;
import com.dayandnight.dayandnightserver.vo.DanmakuVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DanmakuController {

    private final DanmakuService danmakuService;

    public DanmakuController(DanmakuService danmakuService) {
        this.danmakuService = danmakuService;
    }

    @GetMapping("/api/posts/{postId}/danmaku")
    public List<DanmakuVO> listForPost(@PathVariable long postId, HttpServletRequest httpRequest) {
        return danmakuService.listForPost(postId, ClientAddresses.resolve(httpRequest));
    }

    @PostMapping("/api/posts/{postId}/danmaku")
    public DanmakuVO createForPost(
            @PathVariable long postId,
            @Valid @RequestBody CreateDanmakuDTO request,
            HttpServletRequest httpRequest) {
        return danmakuService.createForPost(postId, request, ClientAddresses.resolve(httpRequest));
    }

    @GetMapping("/api/danmaku/map")
    public List<DanmakuVO> listMap(HttpServletRequest httpRequest) {
        return danmakuService.listMap(ClientAddresses.resolve(httpRequest));
    }

    @PostMapping("/api/danmaku")
    public DanmakuVO createForMap(@Valid @RequestBody CreateDanmakuDTO request, HttpServletRequest httpRequest) {
        return danmakuService.createForMap(request, ClientAddresses.resolve(httpRequest));
    }

    @GetMapping("/api/danmaku")
    public List<DanmakuVO> recent(HttpServletRequest httpRequest) {
        return danmakuService.recent(ClientAddresses.resolve(httpRequest));
    }

    @PostMapping("/api/danmaku/{id}/like")
    public DanmakuVO like(@PathVariable long id, HttpServletRequest httpRequest) {
        return danmakuService.toggleLike(id, ClientAddresses.resolve(httpRequest));
    }

    @PatchMapping("/api/danmaku/{id}")
    public DanmakuVO update(@PathVariable long id, @Valid @RequestBody UpdateCommentDTO request, HttpServletRequest httpRequest) {
        return danmakuService.update(id, request, ClientAddresses.resolve(httpRequest));
    }

    @DeleteMapping("/api/danmaku/{id}")
    public void delete(@PathVariable long id) {
        danmakuService.delete(id);
    }
}
