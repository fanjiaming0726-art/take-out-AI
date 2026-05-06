package com.example.fjm0313_takeout_self.mongo.repository;

import com.example.fjm0313_takeout_self.mongo.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByDishIdOrderByCreateTimeDesc(Long dishId);

    List<Review> findByUserIdOrderByCreateTimeDesc(Long userId);

    boolean existsByOrderIdAndDishIdAndUserId(Long orderId, Long dishId, Long userId);
}