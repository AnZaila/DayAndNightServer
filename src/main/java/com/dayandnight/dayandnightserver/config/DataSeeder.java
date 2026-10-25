package com.dayandnight.dayandnightserver.config;

import com.dayandnight.dayandnightserver.enums.CommentMode;
import com.dayandnight.dayandnightserver.mapper.SiteSettingMapper;
import com.dayandnight.dayandnightserver.mapper.UserMapper;
import com.dayandnight.dayandnightserver.pojo.SiteSetting;
import com.dayandnight.dayandnightserver.pojo.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserMapper userMapper;
    private final SiteSettingMapper siteSettingMapper;
    private final PasswordEncoder passwordEncoder;
    private final DayAndNightProperties properties;

    public DataSeeder(
            UserMapper userMapper,
            SiteSettingMapper siteSettingMapper,
            PasswordEncoder passwordEncoder,
            DayAndNightProperties properties) {
        this.userMapper = userMapper;
        this.siteSettingMapper = siteSettingMapper;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userMapper.count() == 0) {
            insertOwner(properties.ownerA());
            insertOwner(properties.ownerB());
        }
        if (siteSettingMapper.find() == null) {
            SiteSetting setting = new SiteSetting();
            setting.setTitle("朝暮");
            setting.setSubtitle("你和我的日子");
            setting.setTogetherSince(properties.togetherSince());
            setting.setAbout("把日子放在这里。想给看的人，从时间轴读起。");
            setting.setCommentMode(CommentMode.OPEN.name());
            siteSettingMapper.insert(setting);
        }
    }

    private void insertOwner(DayAndNightProperties.Owner owner) {
        if (owner == null || owner.email() == null || owner.password() == null || owner.name() == null) {
            throw new IllegalStateException("请在 application-local.yaml 里写上两个主人的邮箱、密码和名字");
        }
        User user = new User();
        user.setEmail(owner.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(owner.password()));
        user.setDisplayName(owner.name().trim());
        user.setRole("OWNER");
        userMapper.insert(user);
    }
}
