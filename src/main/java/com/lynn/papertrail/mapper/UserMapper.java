package com.lynn.papertrail.mapper;

import com.lynn.papertrail.entity.User;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户映射器
 *
 * @author lynn
 * @since 2025-10-02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    User selectByUsername(@Param("username") String username);
    
    User selectByEmail(@Param("email") String email);
}