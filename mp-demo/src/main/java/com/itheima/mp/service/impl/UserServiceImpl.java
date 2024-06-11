package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 扣减余额
        // this.baseMapper.deductBalance(id, money);
        int i = user.getBalance() - money;
        this.lambdaUpdate()
            .set(User::getBalance, i)
            .set(i == 0, User::getStatus, 2)
            .eq(User::getId, id)
            .eq(User::getBalance, user.getBalance()) // 乐观锁
            .update();
    }

    @Override
    public List<User> getList(UserQuery userQuery) {
        String name = userQuery.getName();
        Integer status = userQuery.getStatus();
        Integer minBalance = userQuery.getMinBalance();
        Integer maxBalance = userQuery.getMaxBalance();

        return this.lambdaQuery()
            .like(name != null, User::getUsername, name)
            .eq(status != null, User::getStatus, status)
            .ge(minBalance != null, User::getBalance, minBalance)
            .le(maxBalance != null, User::getBalance, maxBalance)
            .list();
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new RuntimeException("报错了");
        }

        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        // Db 静态工具
        List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, id).list();

        if (addresses != null && !addresses.isEmpty()) {
            List<AddressVO> addressVOS = BeanUtil.copyToList(addresses, AddressVO.class);
            userVO.setAddresses(addressVOS);
        }
        return userVO;
    }
}
