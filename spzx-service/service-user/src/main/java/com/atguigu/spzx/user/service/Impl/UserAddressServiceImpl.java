package com.atguigu.spzx.user.service.Impl;

import com.atguigu.spzx.model.entity.user.UserAddress;
import com.atguigu.spzx.user.mapper.UserAddressMapper;
import com.atguigu.spzx.user.service.UserAddressService;
import com.atguigu.spzx.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> findUserAddressList() {
        Long id = AuthContextUtil.getUserInfo().getId();
        List<UserAddress> userAddresses = userAddressMapper.findUserAddressList(id);
        return userAddresses;
    }

    @Override
    public UserAddress getById(Long id) {
        return userAddressMapper.getById(id);
    }
}
