package com.dayandnight.dayandnightserver.service.impl;

import com.dayandnight.dayandnightserver.common.OwnerUser;
import com.dayandnight.dayandnightserver.mapper.UserMapper;
import com.dayandnight.dayandnightserver.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OwnerDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public OwnerDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("邮箱或密码不正确");
        }
        return new OwnerUser(user.getId(), user.getEmail(), user.getPasswordHash(), user.getDisplayName(), user.getAvatarUrl());
    }
}
