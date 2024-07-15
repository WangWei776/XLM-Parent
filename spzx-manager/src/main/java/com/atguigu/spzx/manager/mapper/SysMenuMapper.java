package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.model.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper {
    List<SysMenu> findNodes();

    void save(SysMenu sysMenu);

    void update(SysMenu sysMenu);

    void delete(Long id);

    int HaveChildrenById(Long id);

    List<SysMenu> findMenusByUserId(Long userId);

    SysMenu selectById(Long parentId);
}
