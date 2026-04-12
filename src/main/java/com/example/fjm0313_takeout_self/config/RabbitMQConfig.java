package com.example.fjm0313_takeout_self.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String SECKILL_EXCHANGE = "seckill.exchange";

    // 队列名称
    public static final String SECKILL_ORDER_QUEUE = "seckill.order.queue";

    // 路由键
    public static final String SECKILL_ORDER_ROUTING_KEY = "seckill.order";

    // 声明交换机（Direct 类型：精确匹配路由键）
    @Bean
    public DirectExchange seckillExchange() {

        return new DirectExchange(SECKILL_EXCHANGE);
    }

    // 声明队列（durable=true，RabbitMQ 重启后队列不丢失）
    @Bean
    public Queue seckillOrderQueue() {
        // 保证可持续，也就是说即使RabbitMQ重启了Queue依然还在，里面的消息还在
        return new Queue(SECKILL_ORDER_QUEUE, true);
    }

    // 把队列绑定到交换机，指定路由键
    @Bean
    public Binding seckillOrderBinding(Queue seckillOrderQueue, DirectExchange seckillExchange) {
        return BindingBuilder.bind(seckillOrderQueue).to(seckillExchange).with(SECKILL_ORDER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}