package com.dayandnight.dayandnightserver.common;

import com.dayandnight.dayandnightserver.common.exception.BadRequestException;
import com.dayandnight.dayandnightserver.config.DayAndNightProperties;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class DiaryImages {

    private static final int MAX_EDGE = 2000;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final DayAndNightProperties properties;
    private String fontFamily;

    public DiaryImages(DayAndNightProperties properties) {
        this.properties = properties;
    }

    public Stored store(MultipartFile file, String watermark) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("请选择图片");
        }
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(type)) {
            throw new BadRequestException("只接收 jpg、png、webp");
        }
        BufferedImage scaled = read(file);
        String filename = UUID.randomUUID() + ".jpg";
        Path target = uploadDirectory().resolve(filename);
        try {
            Files.createDirectories(target.getParent());
            BufferedImage image = toRgb(scaled);
            paintWatermark(image, watermark);
            writeJpeg(image, target);
            return new Stored("/uploads/" + filename, image.getWidth(), image.getHeight());
        } catch (RuntimeException | IOException exception) {
            deleteQuietly(target);
            if (exception instanceof BadRequestException badRequest) {
                throw badRequest;
            }
            throw new BadRequestException("图片处理失败");
        }
    }

    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        String filename = url.substring("/uploads/".length());
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return;
        }
        deleteQuietly(uploadDirectory().resolve(filename));
    }

    private BufferedImage read(MultipartFile file) {
        try {
            BufferedImage image = Thumbnails.of(file.getInputStream()).size(MAX_EDGE, MAX_EDGE).asBufferedImage();
            if (image == null || image.getWidth() < 1 || image.getHeight() < 1) {
                throw new BadRequestException("这张图无法读取");
            }
            return image;
        } catch (IOException exception) {
            throw new BadRequestException("这张图无法读取");
        }
    }

    private static BufferedImage toRgb(BufferedImage source) {
        BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgb.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            graphics.drawImage(source, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return rgb;
    }

    private void paintWatermark(BufferedImage image, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int size = Math.max(14, Math.min(28, width / 38));
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font = new Font(family(), Font.PLAIN, size);
            graphics.setFont(font);
            FontMetrics metrics = graphics.getFontMetrics();
            while (size > 12 && metrics.stringWidth(text) > width - 24) {
                size -= 1;
                font = font.deriveFont((float) size);
                graphics.setFont(font);
                metrics = graphics.getFontMetrics();
            }
            int padX = Math.max(10, size / 2);
            int padY = Math.max(6, size / 3);
            int boxWidth = metrics.stringWidth(text) + padX * 2;
            int boxHeight = metrics.getHeight() + padY;
            int margin = Math.max(12, width / 40);
            int x = Math.max(8, width - boxWidth - margin);
            int y = Math.max(8, height - boxHeight - margin);
            graphics.setColor(new Color(20, 16, 40, 150));
            graphics.fillRoundRect(x, y, boxWidth, boxHeight, boxHeight, boxHeight);
            graphics.setColor(Color.WHITE);
            graphics.drawString(text, x + padX, y + padY / 2 + metrics.getAscent());
        } finally {
            graphics.dispose();
        }
    }

    private String family() {
        if (fontFamily != null) {
            return fontFamily;
        }
        String[] preferred = {"Microsoft YaHei", "PingFang SC", "Noto Sans CJK SC", "Source Han Sans SC", "WenQuanYi Micro Hei", "SimHei"};
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String name : preferred) {
            for (String candidate : available) {
                if (candidate.equalsIgnoreCase(name)) {
                    fontFamily = candidate;
                    return fontFamily;
                }
            }
        }
        fontFamily = Font.SANS_SERIF;
        return fontFamily;
    }

    private static void writeJpeg(BufferedImage image, Path target) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.86f);
        try (ImageOutputStream output = ImageIO.createImageOutputStream(target.toFile())) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    private void deleteQuietly(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // 磁盘上的临时文件删不掉时，不挡住后面的提示。
        }
    }

    private Path uploadDirectory() {
        Path path = Path.of(properties.uploadDir());
        if (!path.isAbsolute()) {
            path = Path.of(System.getProperty("user.dir")).resolve(path);
        }
        return path;
    }

    public record Stored(String url, int width, int height) {
    }
}
