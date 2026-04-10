package com.example.fjm0313_takeout_self.service;

import com.example.fjm0313_takeout_self.entity.SeckillActivity;

import java.util.List;

public interface SeckillService {
    void loadActivityToRedis(Long activityId);
    int trySeckill(Long activityId, Long userId);
    List<SeckillActivity> listActivities();
    SeckillActivity findById(Long id);
}