package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int count();

    int insert(User user);

    User findByEmail(String email);

    User findById(long id);

    int updateProfile(User user);
}
