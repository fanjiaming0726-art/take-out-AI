package com.example.fjm0313_takeout_self.common.MQ.message;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderNotifyMessage implements Serializable {

    private Long orderId;

    private String orderNumber;

    private Long userId;

    private String username;

    private BigDecimal amount;

    private String consignee;

    private String phone;

    private String address;

    private String remark;
}