package com.dayandnight.dayandnightserver.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 在 Spring 启动前读取工作目录里的 env 文件。已存在的系统环境变量优先，后面的文件覆盖前面文件里的同名项。
 */
public final class EnvFiles {

    private static final Set<String> FROM_FILES = new HashSet<>();

    private EnvFiles() {
    }

    public static void load(String[] args) {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile == null || profile.isBlank()) {
            profile = System.getProperty("spring.profiles.active");
        }
        if (profile == null || profile.isBlank()) {
            profile = profileFromArgs(args);
        }
        if (profile == null || profile.isBlank()) {
            profile = "dev";
            System.setProperty("spring.profiles.active", profile);
        }
        loadFile(Path.of(".env"));
        loadFile(Path.of("prod".equals(profile) ? ".env.production" : ".env.development"));
        loadFile(Path.of(".env.local"));
    }

    private static String profileFromArgs(String[] args) {
        if (args == null) {
            return null;
        }
        String prefix = "--spring.profiles.active=";
        for (String arg : args) {
            if (arg != null && arg.startsWith(prefix)) {
                return arg.substring(prefix.length()).trim();
            }
        }
        return null;
    }

    private static void loadFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("无法读取 " + path.toAbsolutePath(), exception);
        }
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("export ")) {
                line = line.substring("export ".length()).trim();
            }
            int split = line.indexOf('=');
            if (split <= 0) {
                continue;
            }
            String key = line.substring(0, split).trim();
            put(key, unquote(line.substring(split + 1).trim()));
        }
    }

    private static void put(String key, String value) {
        if (System.getenv(key) != null) {
            return;
        }
        if (System.getProperty(key) != null && !FROM_FILES.contains(key)) {
            return;
        }
        System.setProperty(key, value);
        FROM_FILES.add(key);
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        int comment = value.indexOf(" #");
        return comment >= 0 ? value.substring(0, comment).trim() : value;
    }
}
