package com.example.fjm0313_takeout_self.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewSubmitDTO {

    private Long orderId;

    private Long dishId;

    private Integer rating;

    private String content;

    private List<String> imageUrls;

    private Boolean anonymous;
}