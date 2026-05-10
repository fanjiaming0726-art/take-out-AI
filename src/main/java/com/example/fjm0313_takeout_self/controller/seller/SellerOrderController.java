package com.example.fjm0313_takeout_self.controller.seller;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.fjm0313_takeout_self.vo.OrdersVO;
import com.example.fjm0313_takeout_self.entity.OrderDetail;
import com.example.fjm0313_takeout_self.entity.Orders;
import com.example.fjm0313_takeout_self.service.OrderDetailService;
import com.example.fjm0313_takeout_self.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
public class SellerOrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    // 只放 sellerList 这个方法
    @LoginRequired("EMPLOYEE")
    @GetMapping("/sellerList")
    public Result<List<OrdersVO>> sellerList(@RequestParam(required = false) Integer status ){
        List<Orders> ordersList = ordersService.findAll(status);

        List<OrdersVO> voList = ordersList.stream().map(order ->{
            OrdersVO vo = new OrdersVO();
            BeanUtils.copyProperties(order,vo);
            List<OrderDetail> detailList = orderDetailService.findByOrderId(order.getId());
            vo.setOrderDetails(detailList);
            return vo;

        }).toList();

        return Result.success(voList);

    }
    // 接单
    @LoginRequired("EMPLOYEE")
    @PutMapping("/accept/{id}")
    public Result<String> accept(@PathVariable Long id) {
        try {
            ordersService.updateStatus(id, 2);
            return Result.success("已接单");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 配送
    @LoginRequired("EMPLOYEE")
    @PutMapping("/deliver/{id}")
    public Result<String> deliver(@PathVariable Long id) {
        try {
            ordersService.updateStatus(id, 3);
            return Result.success("配送中");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 完成
    @LoginRequired("EMPLOYEE")
    @PutMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id) {
        try {
            ordersService.updateStatus(id, 4);
            return Result.success("已完成");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 取消
    @LoginRequired("EMPLOYEE")
    @PutMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable Long id) {
        try {
            ordersService.updateStatus(id, 5);
            return Result.success("已取消");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
