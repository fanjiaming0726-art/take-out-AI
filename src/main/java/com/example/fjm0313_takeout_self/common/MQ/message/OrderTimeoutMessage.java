package com.example.fjm0313_takeout_self.common.MQ.message;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderTimeoutMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单类型："NORMAL" 普通订单，"SECKILL" 秒杀订单
     */
    private String orderType;
}