package com.example.fjm0313_takeout_self.common.MQ.sender;

import com.example.fjm0313_takeout_self.common.MQ.message.OrderTimeoutMessage;
import com.example.fjm0313_takeout_self.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderTimeoutSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送延迟消息，消息会在延迟队列中等待 TTL 到期后被转发到死信队列
     */
    public void sendOrderTimeoutMessage(Long orderId, String orderType) {
        OrderTimeoutMessage message = new OrderTimeoutMessage();
        message.setOrderId(orderId);
        message.setOrderType(orderType);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                message
        );

        System.out.println("发送订单超时消息：orderId=" + orderId + ", type=" + orderType + ", 将在15分钟后检查");
    }
}