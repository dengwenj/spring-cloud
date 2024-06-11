package com.itheima.mp.service.impl;

import com.itheima.mp.domain.po.Address;
import com.itheima.mp.mapper.AddressMapper;
import com.itheima.mp.service.IAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 朴睦
 * @since 2024-06-11
 */
@Service
// TODO 利用反射获取泛型 Address 的信息
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
