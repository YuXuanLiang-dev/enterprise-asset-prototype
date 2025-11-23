package com.guangruan.asset.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.guangruan.asset.model.User;

@Mapper
public interface UserMapper {

    @Select("SELECT id, phone, password, name FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);

    @Select("SELECT id, phone, password, name FROM users WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE users SET name = #{name}, phone = #{phone} WHERE id = #{id}")
    int updateProfile(User user);

    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(User user);
}
