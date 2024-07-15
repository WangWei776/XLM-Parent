package com.atguigu.spzx.manager.utils;

import com.atguigu.spzx.model.entity.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList){
        List<SysMenu> result = new ArrayList<>();
        //递归实现
        for (SysMenu sysMenu : sysMenuList){
            if(sysMenu.getParentId().longValue() == 0){
                result.add(findChildren(sysMenu, sysMenuList));
            }
        }
        return result;
    }

    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<>());
        for (SysMenu sysmenu : sysMenuList){
            if (sysMenu.getId().longValue() == sysmenu.getParentId().longValue()){
                sysMenu.getChildren().add(findChildren(sysmenu, sysMenuList));
            }
        }
        return sysMenu;
    }
}