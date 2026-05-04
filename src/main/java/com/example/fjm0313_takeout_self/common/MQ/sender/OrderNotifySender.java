package com.example.fjm0313_takeout_self.common.MQ.sender;

import com.example.fjm0313_takeout_self.common.MQ.message.OrderNotifyMessage;
import com.example.fjm0313_takeout_self.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderNotifySender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendNewOrderMessage(OrderNotifyMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_NOTIFY_EXCHANGE,
                RabbitMQConfig.ORDER_NOTIFY_ROUTING_KEY,
                message
        );

        System.out.println("已发送新订单通知消息：orderId=" + message.getOrderId());
    }
}