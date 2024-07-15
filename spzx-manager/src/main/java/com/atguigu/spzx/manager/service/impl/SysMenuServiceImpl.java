package com.atguigu.spzx.manager.service.impl;

import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMenuMapper;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.service.SysMenuService;
import com.atguigu.spzx.manager.service.SysRoleMenuService;
import com.atguigu.spzx.manager.utils.MenuHelper;
import com.atguigu.spzx.model.entity.system.SysMenu;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.SysMenuVo;
import com.atguigu.spzx.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        //查询所有的菜单
        List<SysMenu> sysMenuList = sysMenuMapper.findNodes();
        if (sysMenuList.isEmpty()) return null;
        //调用工具类,封装符合要求的list集合
        return MenuHelper.buildTree(sysMenuList);
    }

    @Override
    public void save(SysMenu sysMenu) {
        sysMenuMapper.save(sysMenu);
        // 新添加一个菜单，那么此时就需要将该菜单所对应的父级菜单设置为半开
        updateSysRoleMenuIsHalf(sysMenu) ;
        return;
    }

    @Override
    public void update(SysMenu sysMenu) {
        sysMenuMapper.update(sysMenu);
        return;
    }

    @Override
    public void delete(Long id) {
        int count = sysMenuMapper.HaveChildrenById(id);
        if (count > 0){
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
        }
        sysMenuMapper.delete(id);
        return;
    }

    @Override
    public List<SysMenuVo> findMenus() {
        //获取当前登陆的用户id
        SysUser sysUser = AuthContextUtil.get();
        Long userId = sysUser.getId();
        //根据用户id查到可以操作的菜单
        List<SysMenu> sysMenus = sysMenuMapper.findMenusByUserId(userId);
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenus);
        return buildMenus(sysMenuTreeList);
    }
    // 将List<SysMenu>对象转换成List<SysMenuVo>对象
    private List<SysMenuVo> buildMenus(List<SysMenu> menus) {

        List<SysMenuVo> sysMenuVoList = new LinkedList<SysMenuVo>();
        for (SysMenu sysMenu : menus) {
            SysMenuVo sysMenuVo = new SysMenuVo();
            sysMenuVo.setTitle(sysMenu.getTitle());
            sysMenuVo.setName(sysMenu.getComponent());
            List<SysMenu> children = sysMenu.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                sysMenuVo.setChildren(buildMenus(children));
            }
            sysMenuVoList.add(sysMenuVo);
        }
        return sysMenuVoList;
    }

    private void updateSysRoleMenuIsHalf(SysMenu sysMenu) {

        // 查询是否存在父节点
        SysMenu parentMenu = sysMenuMapper.selectById(sysMenu.getParentId());

        if(parentMenu != null) {

            // 将该id的菜单设置为半开
            sysRoleMenuMapper.updateSysRoleMenuIsHalf(parentMenu.getId()) ;

            // 递归调用
            updateSysRoleMenuIsHalf(parentMenu) ;

        }

    }
}
