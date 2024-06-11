package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Api(tags = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("新增用户")
    public void save(@RequestBody UserFormDTO userFormDTO) {
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        userService.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public void delete(@ApiParam("用户id") @PathVariable Long id) {
        userService.removeById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 id 查询用户")
    public UserVO getUserById(@ApiParam("用户id") @PathVariable Long id) {
        User user = userService.getById(id);
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @GetMapping
    @ApiOperation("根据 id 批量查询")
    public List<UserVO> getUserByIds(List<Long> ids) {
        List<User> users = userService.listByIds(ids);
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @PutMapping("/{id}/deduction/{money}")
    @ApiOperation("根据 id 扣减余额")
    public void deductBalance(@PathVariable Long id, @PathVariable Integer money) {
        userService.deductBalance(id, money);
    }

    @GetMapping("/list")
    @ApiOperation("条件查询")
    public List<User> getList(UserQuery userQuery) {
        return userService.getList(userQuery);
    }
}
