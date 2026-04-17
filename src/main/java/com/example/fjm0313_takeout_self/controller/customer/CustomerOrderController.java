package com.example.fjm0313_takeout_self.controller.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.common.UserContext;
import com.example.fjm0313_takeout_self.vo.OrdersVO;
import com.example.fjm0313_takeout_self.entity.*;
import com.example.fjm0313_takeout_self.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("customer/orders")
public class CustomerOrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @LoginRequired("CUSTOMER")
    @PostMapping("/submit")
    public Result<Orders> submit(@RequestBody Orders orders){
        try {
            Long userId = UserContext.getUserId();
            Orders result = ordersService.submitOrder(userId, orders.getAddressBookId(), orders.getRemark());
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/userOrderList")
    public Result<List<OrdersVO>> userOrdersList(){
        Long userId = UserContext.getUserId();

        List<Orders> ordersList = ordersService.findByUserId(userId);
        List<OrdersVO> ordersVOList = ordersList.stream().map(order -> {
            OrdersVO ordersVO = new OrdersVO();
            BeanUtils.copyProperties(order,ordersVO);
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
            ordersVO.setOrderDetails(orderDetails);
            return ordersVO;
        }).toList();

        return Result.success(ordersVOList);
    }

    @LoginRequired("CUSTOMER")
    @PutMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        try {
            Long userId = UserContext.getUserId();
            Orders order = ordersService.findById(id);
            if (order == null) {
                return Result.fail("订单不存在");
            }
            if (!order.getUserId().equals(userId)) {
                return Result.fail("无权操作此订单");
            }
            if (order.getStatus() != 0) {
                return Result.fail("订单状态不正确，无法支付");
            }
            ordersService.updateStatus(id, 1);
            return Result.success("支付成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }


    

}
