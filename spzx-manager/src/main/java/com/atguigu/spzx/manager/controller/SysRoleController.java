package com.atguigu.spzx.manager.controller;


import com.atguigu.spzx.manager.service.SysRoleService;
import com.atguigu.spzx.model.dto.system.SysRoleDto;
import com.atguigu.spzx.model.entity.system.SysRole;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    //查询所有角色or查询用户已有的角色
    @GetMapping("/findAllRoles/{userId}")
    public Result findAllRoles(@PathVariable("userId")Long userId){
        Map<String, Object> map = sysRoleService.findAllRoles(userId);
        return Result.build(map, ResultCodeEnum.SUCCESS);
    }

    //删除角色功能
    @DeleteMapping("/deleteById/{roleId}")
    public Result deleteById(@PathVariable(value = "roleId") Long roleId){
        sysRoleService.deleteById(roleId);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //修改角色方法
    @PutMapping("/updateSysRole")
    public Result updateSysRole(@RequestBody SysRole sysRole){
        sysRoleService.updateSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //添加角色的方法
    @PostMapping("/saveSysRole")
    public Result saveSysRole(@RequestBody SysRole sysRole){
        sysRoleService.saveSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //角色列表方法
    //current 当前页;limit每页条数
    @PostMapping("/findByPage/{current}/{limit}")
    public Result findByPage(@PathVariable("current") Integer current,
                             @PathVariable("limit") Integer limit,
                             @RequestBody SysRoleDto sysRoleDto) {
        PageInfo<SysRole> pageInfo = sysRoleService.findByPage(current, limit, sysRoleDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
