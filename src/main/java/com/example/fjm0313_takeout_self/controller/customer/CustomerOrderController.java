package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.vo.OrdersVO;
import com.example.fjm0313_takeout_self.entity.*;
import com.example.fjm0313_takeout_self.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String result = ordersService.pay(id);
        if (result.equals("支付成功")){
            return Result.success(result);
        }else {
            return Result.fail(result);
        }
    }


    

}
