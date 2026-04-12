package com.example.fjm0313_takeout_self.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Long activityId;

    private Long dishId;

    private String dishName;

    private BigDecimal seckillPrice;

    private String consignee;

    private String phone;

    private String address;

    // 0未支付 1已支付 2已取消
    private Integer status;

    private String orderNumber;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}