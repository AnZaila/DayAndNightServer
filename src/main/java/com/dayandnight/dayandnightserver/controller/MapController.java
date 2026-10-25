package com.dayandnight.dayandnightserver.controller;

import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.config.DayAndNightProperties;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

@RestController
public class MapController {

    /** 与前端 MapPoint 的地图宽高一致，点击换算依赖这个尺寸。 */
    private static final int MAP_WIDTH = 960;
    private static final int MAP_HEIGHT = 400;

    private final DayAndNightProperties properties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public MapController(DayAndNightProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/api/maps/static")
    public ResponseEntity<byte[]> image(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "4") int zoom,
            @RequestParam(required = false) Double markerLng,
            @RequestParam(required = false) Double markerLat) {
        String key = properties.amapKey();
        if (key == null || key.isBlank()) {
            throw new BadRequestException("还没有配置高德地图密钥");
        }
        checkPoint(lng, lat);
        if (zoom < 1 || zoom > 17) {
            throw new BadRequestException("地图缩放超出范围");
        }
        String markers = marker(markerLng, markerLat);
        String url = "https://restapi.amap.com/v3/staticmap?location=" + coord(lng) + "," + coord(lat)
                + "&zoom=" + zoom
                + "&size=" + MAP_WIDTH + "*" + MAP_HEIGHT
                + "&scale=1"
                + (markers == null ? "" : "&markers=" + URLEncoder.encode(markers, StandardCharsets.UTF_8))
                + "&key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "image/png,image/jpeg,*/*")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] body = response.body();
            MediaType type = imageType(body);
            if (response.statusCode() != 200 || type == null) {
                throw new BadRequestException("地图暂时打不开");
            }
            return ResponseEntity.ok()
                    .contentType(type)
                    .cacheControl(CacheControl.maxAge(Duration.ofHours(12)).cachePublic())
                    .body(body);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("地图暂时打不开");
        } catch (IOException exception) {
            throw new BadRequestException("地图暂时打不开");
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("地图暂时打不开");
        }
    }

    private static String marker(Double markerLng, Double markerLat) {
        if (markerLng == null && markerLat == null) {
            return null;
        }
        if (markerLng == null || markerLat == null) {
            throw new BadRequestException("地点需要同时标记纬度和经度");
        }
        checkPoint(markerLng, markerLat);
        return "large,0x4338ca,:" + coord(markerLng) + "," + coord(markerLat);
    }

    private static void checkPoint(double lng, double lat) {
        if (!Double.isFinite(lat) || lat < -90 || lat > 90) {
            throw new BadRequestException("纬度超出范围");
        }
        if (!Double.isFinite(lng) || lng < -180 || lng > 180) {
            throw new BadRequestException("经度超出范围");
        }
    }

    private static String coord(double value) {
        return String.format(Locale.US, "%.6f", value);
    }

    private static MediaType imageType(byte[] body) {
        if (body == null || body.length < 8) {
            return null;
        }
        if ((body[0] & 0xFF) == 0x89 && body[1] == 0x50 && body[2] == 0x4E && body[3] == 0x47) {
            return MediaType.IMAGE_PNG;
        }
        if ((body[0] & 0xFF) == 0xFF && (body[1] & 0xFF) == 0xD8) {
            return MediaType.IMAGE_JPEG;
        }
        return null;
    }
}
