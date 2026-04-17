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
public class Orders implements Serializable {
    private static final Long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String username;

    private Long userId;

    // 由于使用double做加减运算会出现精度问题，所以使用BigDecimal
    private BigDecimal amount;

    // 收货人
    private String consignee;

    // 订单号
    private String number;

    // 地址
    private String address;

    private String phone;


    // 订单状态I（0 未支付/1 已支付/2 已接单/3 配送中/4 已完成/5 已取消）
    private Integer status;

    private LocalDateTime checkoutTime;

    // 备注
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 不插入数据库
    @TableField(exist = false)
    private Long addressBookId;


}
