package com.example.fjm0313_takeout_self.service;

import com.example.fjm0313_takeout_self.dto.ReviewSubmitDTO;
import com.example.fjm0313_takeout_self.mongo.Review;

import java.util.List;

public interface ReviewService {

    Review submitReview(Long userId, ReviewSubmitDTO dto);

    List<Review> listByDishId(Long dishId);

    List<Review> listMyReviews(Long userId);
}