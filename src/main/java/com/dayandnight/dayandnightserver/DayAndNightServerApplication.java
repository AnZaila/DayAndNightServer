package com.dayandnight.dayandnightserver;

import com.dayandnight.dayandnightserver.config.DayAndNightProperties;
import com.dayandnight.dayandnightserver.config.EnvFiles;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.dayandnight.dayandnightserver.mapper")
@EnableConfigurationProperties(DayAndNightProperties.class)
public class DayAndNightServerApplication {

    public static void main(String[] args) {
        EnvFiles.load(args);
        SpringApplication.run(DayAndNightServerApplication.class, args);
    }
}
