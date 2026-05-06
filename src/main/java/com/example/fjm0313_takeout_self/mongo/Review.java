package com.example.fjm0313_takeout_self.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "review")
public class Review {

    // 为什么是String类型的Id，因为mongoDB内的document的id就是一串数字+字符的字符串
    @Id
    private String id;

    private Long orderId;

    private Long userId;

    private String username;

    private Long dishId;

    private String dishName;

    private String dishImage;

    /**
     * 评分：1 ~ 5
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片，先预留，第一版可以不传
     */
    private List<String> imageUrls;

    /**
     * 菜品价格，方便评价列表展示
     */
    private BigDecimal dishAmount;

    /**
     * 是否匿名
     */
    private Boolean anonymous;

    private LocalDateTime createTime;
}