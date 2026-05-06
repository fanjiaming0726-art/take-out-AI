package com.example.fjm0313_takeout_self.controller.customer;

import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.common.UserContext;
import com.example.fjm0313_takeout_self.dto.ReviewSubmitDTO;
import com.example.fjm0313_takeout_self.mongo.Review;
import com.example.fjm0313_takeout_self.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/reviews")
public class CustomerReviewController {

    @Autowired
    private ReviewService reviewService;

    @LoginRequired("CUSTOMER")
    @PostMapping
    public Result<Review> submit(@RequestBody ReviewSubmitDTO dto) {
        try {
            Long userId = UserContext.getUserId();
            Review review = reviewService.submitReview(userId, dto);
            return Result.success(review);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @LoginRequired("CUSTOMER")
    @GetMapping("/my")
    public Result<List<Review>> myReviews() {
        Long userId = UserContext.getUserId();
        return Result.success(reviewService.listMyReviews(userId));
    }

    @GetMapping("/dish/{dishId}")
    public Result<List<Review>> listByDishId(@PathVariable Long dishId) {
        try {
            return Result.success(reviewService.listByDishId(dishId));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}