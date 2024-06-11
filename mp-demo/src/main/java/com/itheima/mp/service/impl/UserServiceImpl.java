package com.itheima.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void deductBalance(Long id, Integer money) {
        // 查询用户
        User user = this.getById(id);
        // 检验用户状态
        if (user == null && user.getStatus() == 2) {
            throw new RuntimeException("出错");
        }
        // 检验余额是否充足
        if (user.getBalance() - money < 0) {
            throw new RuntimeException("不够");
        }
        // 扣减余额
        this.baseMapper.deductBalance(id, money);
    }
}
