package com.atguigu.spzx.manager.service.impl;

import cn.hutool.http.server.HttpServerResponse;
import com.alibaba.excel.EasyExcel;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.Listener.ExcelListener;
import com.atguigu.spzx.manager.mapper.CategoryMapper;
import com.atguigu.spzx.manager.service.CategoryService;
import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.product.CategoryExcelVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findByParentId(Long parentId) {
        // 根据分类id查询它下面的所有的子分类数据
        List<Category> list = categoryMapper.findByParentId(parentId);
        if (!list.isEmpty()){
            //setHasChildren
            for (Category item : list){
                int count = categoryMapper.countByParentId(item.getId());
                if (count > 0){
                    item.setHasChildren(true);
                }else {
                    item.setHasChildren(false);
                }
            }
        }
        return list;
    }

    //文件导出
    @Override
    public void exportData(HttpServletResponse response) {

        try{
            //设置响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("分类数据", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //查询所有分类数据
            List<Category> categoryList = categoryMapper.findAll();
            List<CategoryExcelVo> categoryExcelVos = new ArrayList<>();
            for (Category category:categoryList){
                CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                BeanUtils.copyProperties(category, categoryExcelVo);
                categoryExcelVos.add(categoryExcelVo);
            }
            //调用easyExcel方法完成写操作
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("分类数据").doWrite(categoryExcelVos);
        }catch (Exception e){
            e.printStackTrace();
            throw  new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

    }

    @Override
    public void importData(MultipartFile file) {
        try {
            ExcelListener<CategoryExcelVo> excelListener = new ExcelListener(categoryMapper);
            EasyExcel.read(file.getInputStream(), CategoryExcelVo.class, excelListener).sheet().doRead();
        }catch (IOException e){
            e.printStackTrace();
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }
}
