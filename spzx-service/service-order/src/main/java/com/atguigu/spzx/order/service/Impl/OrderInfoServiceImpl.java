package com.atguigu.spzx.order.service.Impl;

import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.feign.cart.CartFeignClient;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.feign.user.UserFeignClient;
import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.entity.order.OrderItem;
import com.atguigu.spzx.model.entity.order.OrderLog;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.entity.user.UserAddress;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import com.atguigu.spzx.order.mapper.OrderInfoMapper;
import com.atguigu.spzx.order.mapper.OrderItemMapper;
import com.atguigu.spzx.order.mapper.OrderLogMapper;
import com.atguigu.spzx.order.service.OrderInfoService;
import com.atguigu.spzx.utils.AuthContextUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Autowired
    private CartFeignClient cartFeignClient ;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public TradeVo trade() {
        //要结算的购物列表数据
        List<CartInfo> allCkecked = cartFeignClient.getAllCkecked();
        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartInfo cartInfo : allCkecked){
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setThumbImg(cartInfo.getImgUrl());
            orderItemList.add(orderItem);
        }
        //结算总金额
        BigDecimal totalAmount = new BigDecimal(0);
        for(OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        return tradeVo;
    }

    @Override
    public Long submitOrder(OrderInfoDto orderInfoDto) {
        //检查库存是否充足
        if (CollectionUtils.isEmpty(orderInfoDto.getOrderItemList())){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        for (OrderItem orderItem : orderInfoDto.getOrderItemList()){
            Long skuId = orderItem.getSkuId();
            ProductSku productSkuBySkuId = productFeignClient.getBySkuId(skuId);
            if (productSkuBySkuId == null){
                throw new GuiguException(ResultCodeEnum.DATA_ERROR);
            }
            if (productSkuBySkuId.getStockNum().intValue() < orderItem.getSkuNum().intValue()){
                throw new GuiguException(ResultCodeEnum.STOCK_LESS);
            }

        }
        //构建订单数据
        //查询收货人地址
        UserAddress userAddress = userFeignClient.getUserAddress(orderInfoDto.getUserAddressId());
        UserInfo userInfo = AuthContextUtil.getUserInfo();
        OrderInfo orderInfo = new OrderInfo();
        //设置用户信息
        orderInfo.setUserId(userInfo.getId());
        orderInfo.setNickName(userInfo.getNickName());
        //设置地址信息
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());
        //订单金额
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderInfoDto.getOrderItemList()) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(totalAmount);
        orderInfo.setFeightFee(orderInfoDto.getFeightFee());
        orderInfo.setPayType(2);
        orderInfo.setOrderStatus(0);
        orderInfo.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        orderInfoMapper.save(orderInfo);
        //保存订单明细
        for (OrderItem orderItem : orderInfoDto.getOrderItemList()) {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.save(orderItem);
        }
        //记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0);
        orderLog.setNote("提交订单");
        orderLogMapper.save(orderLog);
        // TODO 远程调用service-cart微服务接口清空购物车数据
        cartFeignClient.deleteChecked();
        //返回订单id
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.getOrderInfo(orderId);
        return orderInfo;
    }

    @Override
    public TradeVo buy(Long skuId) {
        // 查询商品
        ProductSku productSku = productFeignClient.getBySkuId(skuId);
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setSkuNum(1);
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setThumbImg(productSku.getThumbImg());
        orderItemList.add(orderItem);

        // 计算总金额
        BigDecimal totalAmount = productSku.getSalePrice();
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);

        // 返回
        return tradeVo;
    }

    @Override
    public PageInfo list(Integer page, Integer limit, Integer orderStatus) {
        PageHelper.startPage(page, limit);
        Long id = AuthContextUtil.getUserInfo().getId();
        List<OrderInfo> orderInfos = orderInfoMapper.list(id, orderStatus);
        for (OrderInfo orderInfo : orderInfos){
            List<OrderItem> orderItem = orderItemMapper.findByOrderId(orderInfo.getId());
            orderInfo.setOrderItemList(orderItem);
        }
        PageInfo<OrderInfo> pageInfo = new PageInfo<>(orderInfos);
        return pageInfo;
    }

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.getByOrderNo(orderNo);
        List<OrderItem> orderItem = orderItemMapper.findByOrderId(orderInfo.getId());
        orderInfo.setOrderItemList(orderItem);
        return orderInfo;
    }

    @Override
    public void updateOrderStatus(String orderNo, Integer orderStatus) {
        // 更新订单状态
        OrderInfo orderInfo = orderInfoMapper.getByOrderNo(orderNo);
        orderInfo.setOrderStatus(1);
        orderInfo.setPayType(orderStatus);
        orderInfo.setPaymentTime(new Date());
        orderInfoMapper.updateById(orderInfo);

        // 记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(1);
        orderLog.setNote("支付宝支付成功");
        orderLogMapper.save(orderLog);
    }
}
