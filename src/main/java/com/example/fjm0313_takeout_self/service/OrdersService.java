package com.example.fjm0313_takeout_self.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.entity.Orders;
import com.example.fjm0313_takeout_self.entity.SeckillActivity;

import java.util.List;

public interface OrdersService {
    void createOrder(Orders orders);
    List<Orders> findByUserId(Long userId);
    List<Orders> findAll(Integer status);
    Orders submitOrder(Long userId,Long addressBookId,String remark);
    void updateStatus(Long orderId, Integer status);
    Orders findById(Long id);
    String pay(Long id);
}