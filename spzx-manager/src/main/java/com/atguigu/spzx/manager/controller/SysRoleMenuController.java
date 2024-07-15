package com.atguigu.spzx.manager.controller;


import com.atguigu.spzx.manager.service.SysRoleMenuService;
import com.atguigu.spzx.model.dto.system.AssginMenuDto;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationSupport;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/system/sysRoleMenu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    //保存角色分配菜单数据
    @PostMapping("/doAssgin")
    public Result doAssgin(@RequestBody AssginMenuDto assginMenuDto){
        sysRoleMenuService.doAssgin(assginMenuDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);

    }

    //查所有菜单和角色已分配菜单
    @GetMapping("/findSysRoleMenuByRoleId/{roleId}")
    public Result findSysRoleMenuByRoleId(@PathVariable("roleId")Long roleId){
        Map<String, Object>map = sysRoleMenuService.findSysRoleMenuByRoleId(roleId);
        return Result.build(map, ResultCodeEnum.SUCCESS);

    }
}
