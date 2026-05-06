package com.example.fjm0313_takeout_self.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.dto.ReviewSubmitDTO;
import com.example.fjm0313_takeout_self.entity.Dish;
import com.example.fjm0313_takeout_self.entity.OrderDetail;
import com.example.fjm0313_takeout_self.entity.Orders;
import com.example.fjm0313_takeout_self.entity.User;
import com.example.fjm0313_takeout_self.mapper.DishMapper;
import com.example.fjm0313_takeout_self.mapper.OrderDetailMapper;
import com.example.fjm0313_takeout_self.mapper.OrdersMapper;
import com.example.fjm0313_takeout_self.mapper.UserMapper;
import com.example.fjm0313_takeout_self.mongo.Review;
import com.example.fjm0313_takeout_self.mongo.repository.ReviewRepository;
import com.example.fjm0313_takeout_self.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Review submitReview(Long userId, ReviewSubmitDTO dto) {
        if (dto.getOrderId() == null) {
            throw new RuntimeException("订单id不能为空");
        }

        if (dto.getDishId() == null) {
            throw new RuntimeException("菜品id不能为空");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("评分必须在1到5之间");
        }

        if (!StringUtils.hasText(dto.getContent())) {
            throw new RuntimeException("评价内容不能为空");
        }

        Orders order = ordersMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("不能评价别人的订单");
        }

        if (order.getStatus() == null || order.getStatus() == 0) {
            throw new RuntimeException("未支付订单不能评价");
        }

        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(OrderDetail::getOrderId, dto.getOrderId());
        detailWrapper.eq(OrderDetail::getDishId, dto.getDishId());
        OrderDetail orderDetail = orderDetailMapper.selectOne(detailWrapper);

        if (orderDetail == null) {
            throw new RuntimeException("该订单中不存在这个菜品，不能评价");
        }

        boolean exists = reviewRepository.existsByOrderIdAndDishIdAndUserId(
                dto.getOrderId(),
                dto.getDishId(),
                userId
        );

        if (exists) {
            throw new RuntimeException("该菜品已经评价过了");
        }

        User user = userMapper.selectById(userId);
        Dish dish = dishMapper.selectById(dto.getDishId());

        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setUserId(userId);
        review.setUsername(user == null ? null : user.getUsername());
        review.setDishId(dto.getDishId());
        review.setDishName(orderDetail.getName());
        review.setDishImage(orderDetail.getImage());
        review.setDishAmount(orderDetail.getAmount());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImageUrls(dto.getImageUrls());
        review.setAnonymous(dto.getAnonymous() != null && dto.getAnonymous());
        review.setCreateTime(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> listByDishId(Long dishId) {
        if (dishId == null) {
            throw new RuntimeException("菜品id不能为空");
        }
        return reviewRepository.findByDishIdOrderByCreateTimeDesc(dishId);
    }

    @Override
    public List<Review> listMyReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
}