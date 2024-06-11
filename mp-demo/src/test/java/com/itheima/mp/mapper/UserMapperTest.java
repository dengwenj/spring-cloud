package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void testInsert() {
        User user = new User();
        user.setId(5L);
        user.setUsername("Lucy");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
        User user = userMapper.selectById(5L);
        System.out.println("user = " + user);
    }

    @Test
    void testSelectById2() {
        User user = userMapper.queryUserById(4L);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateById(user);
    }

    @Test
    void testDeleteUser() {
        userMapper.deleteById(5L);
    }

    @Test
    void testSelectByQueryWrapper() {
        // 条件构建器
        Wrapper<User> wrapper = new QueryWrapper<User>()
            .select("id", "username", "info", "balance")
            .like("username", "o")
            .ge("balance", 2000);
        List<User> users = userMapper.selectList(wrapper);
        System.out.println("users" + users);
    }

    @Test
    void testSelectByLambdaQueryWrapper() {
        // 条件构建器
        Wrapper<User> wrapper = new LambdaQueryWrapper<User>()
            .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
            .like(User::getUsername, "o")
            .ge(User::getBalance, 2000);
        List<User> users = userMapper.selectList(wrapper);
        System.out.println("users" + users);
    }

    @Test
    void testUpdateByQueryWrapper() {
        User user = new User();
        user.setBalance(2000);
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("username", "jack");
        userMapper.update(user, wrapper);
    }

    @Test
    void testUpdateByUpdateWrapper() {
        Wrapper<User> wrapper = new UpdateWrapper<User>()
            .setSql("balance = balance - 200")
            .in("id", List.of(1L, 2L, 3L, 4L));
        userMapper.update(null, wrapper);
    }

    @Test
    void testUpdateBalanceByIds() {
        Wrapper<User> wrapper = new QueryWrapper<User>().in("id", List.of(1L, 2L, 3L, 4L));
        userMapper.updateBalanceByIds(wrapper, 200);
    }

    @Test
    void testInsert1() {
        User user = new User();
        user.setId(5L);
        user.setUsername("Lucy");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userService.save(user);
    }

    @Test
    void testGetList() {
        List<User> users = userService.listByIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }
}