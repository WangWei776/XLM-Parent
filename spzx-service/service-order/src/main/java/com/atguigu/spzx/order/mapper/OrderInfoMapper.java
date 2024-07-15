package com.atguigu.spzx.order.mapper;

import com.atguigu.spzx.model.entity.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderInfoMapper {
    void save(OrderInfo orderInfo);

    OrderInfo getOrderInfo(Long orderId);

    List<OrderInfo> list(Long id, Integer orderStatus);

    OrderInfo getByOrderNo(String orderNo) ;

    void updateById(OrderInfo orderInfo);
}
