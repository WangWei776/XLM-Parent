package com.atguigu.spzx.manager.mapper;

import com.atguigu.spzx.model.dto.system.AssginMenuDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper {
    List<Long> selectSysRoleMenuByRoleId(Long roleId);

    void delete(Long roleId);

    void insert(AssginMenuDto assginMenuDto);

    void updateSysRoleMenuIsHalf(Long id);
}
