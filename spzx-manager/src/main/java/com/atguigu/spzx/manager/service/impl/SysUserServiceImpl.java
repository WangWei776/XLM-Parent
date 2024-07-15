package com.atguigu.spzx.manager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysRoleUserMapper;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.dto.system.AssginRoleDto;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.dto.system.SysUserDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    //用户退出
    @Override
    public void logout(String token) {
        redisTemplate.delete("user:login" + token);
        return;
    }

    //用户登录
    @Override
    public LoginVo login(LoginDto loginDto) {

        //0 校验验证码是否有效
        String captcha = loginDto.getCaptcha();
        String codeKey = loginDto.getCodeKey();
        String code = redisTemplate.opsForValue().get("user:login:validate" + codeKey);
        if (!code.equalsIgnoreCase(captcha) || StrUtil.isEmpty(code)){
            throw new GuiguException(ResultCodeEnum.VALIDATECODE_ERROR);
        }

        //SysOperLogMapper.xml 获取应户名
        String userName = loginDto.getUserName();
        //2 根据用户名查询sys_user表
        SysUser user = sysUserMapper.selectByUserName(userName);
        //3 如果根据用户名查不到信息,用户不存在,返回错误信息
        if (user == null){
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        //4 查到用户信息,用户存在
        //5 比较密码是否一致
        String databasePassword = user.getPassword();
        String inputPassword =
                DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes());
        //6 密码一致,成功,否则失败
        if(!databasePassword.equals(inputPassword)){
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        //7 登陆成功,生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        //8 登陆成功用户信息放入redis
        redisTemplate.opsForValue().set("user:login" + token ,
                JSON.toJSONString(user) , 30 , TimeUnit.MINUTES);
        //9 返回loginVo对象
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }

    //获取登录用户信息
    @Override
    public SysUser getUserInfo(String token) {
        String userJson = redisTemplate.opsForValue().get("user:login" + token);
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    @Override
    public PageInfo<SysUser> findByPage(Integer pageNum, Integer pageSize, SysUserDto sysUserDto) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.findByPage(sysUserDto);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void saveSysUser(SysUser sysUser) {
        //判断用户名是否重复
        String userName = sysUser.getUserName();
        SysUser dbUser = sysUserMapper.selectByUserName(userName);
        if(dbUser != null) {
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        //加密密码
        String passWord = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(passWord);
        sysUser.setStatus(1);
        //添加数据
        sysUserMapper.save(sysUser);
    }

    @Override
    public void updateSysUser(SysUser sysUser) {
        //修改用户名时不能与其他用户重复
        String userName = sysUser.getUserName();
        SysUser dbUser = sysUserMapper.selectByUserName(userName);
        if(dbUser != null && dbUser.getId() != sysUser.getId()) {
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        //修改
        sysUserMapper.update(sysUser);
    }

    @Override
    public void deleteById(Long userId) {
        sysUserMapper.delete(userId);
    }

    @Override
    public void doAssign(AssginRoleDto assginRoleDto) {
        //删除用户之前的角色数据
        sysRoleUserMapper.deleteByUserId(assginRoleDto.getUserId());
        //重新分配新的角色数据
        List<Long> roleIdList = assginRoleDto.getRoleIdList();
        for (Long id : roleIdList)
        sysRoleUserMapper.doAssign(assginRoleDto.getUserId(), id);
    }
}
