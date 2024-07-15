package com.atguigu.spzx.manager.service;

import com.atguigu.spzx.model.dto.order.OrderStatisticsDto;
import com.atguigu.spzx.model.vo.order.OrderStatisticsVo;

import java.util.List;

public interface OrderInfoService {
    OrderStatisticsVo getOrderStatisticsData(OrderStatisticsDto orderStatisticsDto);
}
