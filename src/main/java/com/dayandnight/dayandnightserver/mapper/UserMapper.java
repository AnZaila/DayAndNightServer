package com.dayandnight.dayandnightserver.mapper;

import com.dayandnight.dayandnightserver.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int count();

    int insert(User user);

    User findByEmail(String email);

    User findById(long id);

    int updateProfile(@Param("id") long id, @Param("displayName") String displayName, @Param("avatarUrl") String avatarUrl);
}
