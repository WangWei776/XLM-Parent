package com.atguigu.spzx.manager.Listener;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.atguigu.spzx.manager.mapper.CategoryMapper;
import com.atguigu.spzx.model.vo.product.CategoryExcelVo;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.util.List;

//监听器
public class ExcelListener<T> extends AnalysisEventListener<T> {

    private CategoryMapper categoryMapper;
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public ExcelListener(CategoryMapper categoryMapper){
        this.categoryMapper = categoryMapper;
    }
    //把每行读取内容封装到t对象中
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        cachedDataList.add(t);
        if (cachedDataList.size() >= BATCH_COUNT){
            //调用方法加入数据库
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //保存数据
        saveData();
    }
    private void saveData(){
        categoryMapper.batchInert((List<CategoryExcelVo>)cachedDataList);
    }
}

