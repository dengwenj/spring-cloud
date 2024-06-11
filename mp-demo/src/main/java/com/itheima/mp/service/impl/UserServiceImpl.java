package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.UserService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void deductBalance(Long id, Integer money) {
        // 查询用户
        User user = this.getById(id);
        // 检验用户状态
        if (user == null && user.getStatus() == UserStatus.FREEZE) {
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
            .set(i == 0, User::getStatus, UserStatus.FREEZE)
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

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        List<User> users = this.listByIds(ids);
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取 userid
        List<Long> userIdList = users.stream().map(User::getId).toList();

        // 获取这些用户id 的所有地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIdList).list();
        List<AddressVO> addressVOS = BeanUtil.copyToList(addresses, AddressVO.class);

        // 根据用户 id 分组
        Map<Long, List<AddressVO>> userIdGroup = new HashMap<>();
        if (addresses != null && !addresses.isEmpty()) {
             userIdGroup = addressVOS.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);

        for (UserVO userVO : userVOS) {
            userVO.setAddresses(userIdGroup.get(userVO.getId()));
        }
        return userVOS;
    }
}
