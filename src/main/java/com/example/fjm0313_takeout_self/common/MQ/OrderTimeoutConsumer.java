package com.example.fjm0313_takeout_self.common.MQ;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.config.RabbitMQConfig;
import com.example.fjm0313_takeout_self.entity.OrderDetail;
import com.example.fjm0313_takeout_self.entity.Orders;
import com.example.fjm0313_takeout_self.entity.SeckillOrder;
import com.example.fjm0313_takeout_self.mapper.OrderDetailMapper;
import com.example.fjm0313_takeout_self.mapper.OrdersMapper;
import com.example.fjm0313_takeout_self.mapper.SeckillActivityMapper;
import com.example.fjm0313_takeout_self.mapper.SeckillOrderMapper;
import com.example.fjm0313_takeout_self.service.DishService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderTimeoutConsumer {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private DishService dishService;

    /**
     * 监听死信队列，消息到达说明已经过了15分钟
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_DLX_QUEUE)
    @Transactional
    public void handleOrderTimeout(OrderTimeoutMessage message) {
        Long orderId = message.getOrderId();
        String orderType = message.getOrderType();

        System.out.println("收到超时检查消息：orderId=" + orderId + ", type=" + orderType);

        if ("NORMAL".equals(orderType)) {
            handleNormalOrderTimeout(orderId);
        } else if ("SECKILL".equals(orderType)) {
            handleSeckillOrderTimeout(orderId);
        }
    }

    /**
     * 普通订单超时取消：恢复菜品库存
     */
    private void handleNormalOrderTimeout(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            return;
        }

        // 只有未支付（status=0）的订单才需要取消
        if (order.getStatus() != 0) {
            System.out.println("订单" + orderId + "状态为" + order.getStatus() + "，无需取消");
            return;
        }

        // 1. 更新订单状态为已取消（5）
        order.setStatus(5);
        ordersMapper.updateById(order);

        // 2. 恢复库存：查询订单明细，逐个恢复
        List<OrderDetail> details = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId)
        );
        for (OrderDetail detail : details) {
            dishService.restoreStock(detail.getDishId(), detail.getNumber());
        }

        System.out.println("普通订单超时取消成功：orderId=" + orderId);
    }

    /**
     * 秒杀订单超时取消：恢复秒杀活动库存
     */
    private void handleSeckillOrderTimeout(Long orderId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectById(orderId);
        if (seckillOrder == null) {
            return;
        }

        // 只有未支付（status=0）的才取消，注意你的秒杀订单消费者里设的是status=1
        // 如果秒杀订单也想支持"先未支付再付款"的流程，需要把消费者里改成status=0
        // 这里兼容两种：status=0 表示未支付需要取消
        if (seckillOrder.getStatus() != 0) {
            System.out.println("秒杀订单" + orderId + "状态为" + seckillOrder.getStatus() + "，无需取消");
            return;
        }

        // 1. 更新状态为已取消（2）
        seckillOrder.setStatus(2);
        seckillOrderMapper.updateById(seckillOrder);

        // 2. 恢复秒杀活动库存
        seckillActivityMapper.restoreStock(seckillOrder.getActivityId());

        System.out.println("秒杀订单超时取消成功：orderId=" + orderId);
    }
}