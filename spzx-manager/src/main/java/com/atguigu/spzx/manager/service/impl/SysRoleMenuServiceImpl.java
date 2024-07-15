package com.atguigu.spzx.manager.service.impl;

import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMenuMapper;
import com.atguigu.spzx.manager.service.SysRoleMenuService;
import com.atguigu.spzx.manager.utils.MenuHelper;
import com.atguigu.spzx.model.dto.system.AssginMenuDto;
import com.atguigu.spzx.model.entity.system.SysMenu;
import kotlin.contracts.ReturnsNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public Map<String, Object> findSysRoleMenuByRoleId(Long roleId) {
        Map<String, Object> map = new HashMap<>();
        //查所有菜单
        List<SysMenu> nodes = sysMenuMapper.findNodes();
        map.put("sysMenuList", MenuHelper.buildTree(nodes));
        //查角色已拥有的菜单
        List<Long> sysMenus = sysRoleMenuMapper.selectSysRoleMenuByRoleId(roleId);
        map.put("roleMenuList", sysMenus);
        return map;
    }

    @Override
    public void doAssgin(AssginMenuDto assginMenuDto) {
        //先删除原始分配的数据
        sysRoleMenuMapper.delete(assginMenuDto.getRoleId());
        //写入新的数据
        List<Map<String, Number>> menuList = assginMenuDto.getMenuIdList();
        if (menuList != null && menuList.size() > 0) {
            sysRoleMenuMapper.insert(assginMenuDto);
        }
        return;
    }
}
