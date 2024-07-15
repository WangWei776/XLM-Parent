package com.atguigu.spzx.order.controller;

import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import com.atguigu.spzx.order.service.OrderInfoService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/order/orderInfo/auth")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @GetMapping("auth/updateOrderStatusPayed/{orderNo}/{orderStatus}")
    public Result updateOrderStatus(@PathVariable(value = "orderNo") String orderNo , @PathVariable(value = "orderStatus") Integer orderStatus) {
        orderInfoService.updateOrderStatus(orderNo , orderStatus);
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping("/getOrderInfoByOrderNo/{orderNo}")
    public Result<OrderInfo> getOrderInfoByOrderNo(@Parameter(name = "orderId", description = "订单id", required = true) @PathVariable String orderNo) {
        OrderInfo orderInfo = orderInfoService.getByOrderNo(orderNo);
        return Result.build(orderInfo, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/{page}/{limit}")
    public Result list(@Parameter(name = "page", description = "当前页码", required = true)
                           @PathVariable Integer page,

                       @Parameter(name = "limit", description = "每页记录数", required = true)
                           @PathVariable Integer limit,

                       @Parameter(name = "orderStatus", description = "订单状态", required = false)
                           @RequestParam(required = false, defaultValue = "") Integer orderStatus){
        PageInfo<OrderInfo> pageInfo = orderInfoService.list(page, limit, orderStatus);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);

    }

    @GetMapping("/buy/{skuId}")
    public Result buy(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable Long skuId) {
        TradeVo tradeVo = orderInfoService.buy(skuId);
        return Result.build(tradeVo, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/{orderId}")
    public Result getOrderInfo(@PathVariable Long orderId){
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return Result.build(orderId, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestBody OrderInfoDto orderInfoDto){
        Long orderId = orderInfoService.submitOrder(orderInfoDto);
        return Result.build(orderId, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/trade")
    public Result trade(){
        TradeVo tradeVo = orderInfoService.trade();
        return Result.build(tradeVo, ResultCodeEnum.SUCCESS);
    }
}
