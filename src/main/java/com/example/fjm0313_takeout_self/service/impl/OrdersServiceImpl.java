package com.example.fjm0313_takeout_self.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.common.MQ.message.OrderNotifyMessage;
import com.example.fjm0313_takeout_self.common.MQ.sender.OrderNotifySender;
import com.example.fjm0313_takeout_self.common.MQ.sender.OrderTimeoutSender;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.common.UserContext;
import com.example.fjm0313_takeout_self.entity.*;
import com.example.fjm0313_takeout_self.mapper.*;
import com.example.fjm0313_takeout_self.service.DishService;
import com.example.fjm0313_takeout_self.service.OrdersService;
import com.example.fjm0313_takeout_self.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrderNotifySender orderNotifySender;

    @Autowired
    private OrderTimeoutSender orderTimeoutSender;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper cartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private RankingService rankingService;

    @Override
    public void createOrder(Orders orders) {
        ordersMapper.insert(orders);
    }

    @Override
    public List<Orders> findByUserId(Long userId) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectList(wrapper);
    }

    @Override
    public List<Orders> findAll(Integer status) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Orders submitOrder(Long userId, Long addressBookId, String remark) {
        // 1. 查购物车
        LambdaQueryWrapper<ShoppingCart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = cartMapper.selectList(cartWrapper);

        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法下单");
        }

        // 2. 查地址
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);
        if (addressBook == null) {
            throw new RuntimeException("地址信息有误，请重新填写");
        }

        // 3. 查用户
        User user = userMapper.selectById(userId);

        for(ShoppingCart cart : cartList){
            dishService.deductStock(cart.getDishId(),cart.getNumber());
        }

        // 4. 计算总金额
        BigDecimal amountAll = BigDecimal.ZERO;
        for (ShoppingCart cart : cartList) {
            amountAll = amountAll.add(cart.getAmount().multiply(new BigDecimal(cart.getNumber())));
        }

        // 5. 构建订单
        Orders orders = new Orders();
        orders.setNumber(UUID.randomUUID().toString().replace("-", ""));
        orders.setStatus(0);  // 未支付
        orders.setUserId(userId);
        orders.setUsername(user.getUsername());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAmount(amountAll);
        orders.setRemark(remark);

        String fullAddress = (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail());
        orders.setAddress(fullAddress);

        ordersMapper.insert(orders);

        // 发送延迟消息，15分钟后检查是否支付
        orderTimeoutSender.sendOrderTimeoutMessage(orders.getId(), "NORMAL");

        // 6. 保存订单明细
        for (ShoppingCart cart : cartList) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orders.getId());
            detail.setName(cart.getName());
            detail.setImage(cart.getImage());
            detail.setDishId(cart.getDishId());
            detail.setNumber(cart.getNumber());
            detail.setAmount(cart.getAmount());
            detail.setFlavor(cart.getFlavor());
            detail.setPortion(cart.getPortion());
            orderDetailMapper.insert(detail);
        }

        // 在清空购物车之前，增加销量
        for (ShoppingCart cart : cartList) {
            rankingService.increaseSales(cart.getDishId(), cart.getName(), cart.getNumber());
        }


        // 7. 清空购物车
        cartMapper.delete(cartWrapper);



        return orders;
    }

    @Override
    public void updateStatus(Long orderId, Integer status) {
        Orders orders = ordersMapper.selectById(orderId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        orders.setStatus(status);
        ordersMapper.updateById(orders);
    }

    @Override
    public Orders findById(Long id) {
        return ordersMapper.selectById(id);
    }

    @Override
    @Transactional
    public String pay(Long id) {
        try {
            Long userId = UserContext.getUserId();
            Orders order = findById(id);
            if (order == null) {
                return "订单不存在";
            }
            if (!order.getUserId().equals(userId)) {
                return "无权操作此订单";
            }
            if (order.getStatus() != 0) {
                return "订单状态不正确，无法支付";
            }

            order.setStatus(1);
            ordersMapper.updateById(order);

            OrderNotifyMessage notifyMessage = new OrderNotifyMessage();
            notifyMessage.setOrderId(order.getId());
            notifyMessage.setOrderNumber(order.getNumber());
            notifyMessage.setUserId(order.getUserId());
            notifyMessage.setUsername(order.getUsername());
            notifyMessage.setAmount(order.getAmount());
            notifyMessage.setConsignee(order.getConsignee());
            notifyMessage.setPhone(order.getPhone());
            notifyMessage.setAddress(order.getAddress());
            notifyMessage.setRemark(order.getRemark());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    orderNotifySender.sendNewOrderMessage(notifyMessage);
                }
            });

            return "支付成功";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }


}