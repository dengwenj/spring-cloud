package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    //void saveUser(User user);
    //
    //void deleteUser(Long id);
    //
    //void updateUser(User user);
    //
    //User queryUserById(@Param("id") Long id);
    //
    //List<User> queryUserByIds(@Param("ids") List<Long> ids);
}
