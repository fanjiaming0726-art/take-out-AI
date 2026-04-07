package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean isAllowed(Long userId, int maxRequests, int windowSeconds) {
        String key = "rate:limit:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - windowSeconds * 1000L;

        // 1. 删除窗口之前的旧记录
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // 2. 统计窗口内有多少次请求
        Long count = redisTemplate.opsForZSet().zCard(key);

        if (count != null && count >= maxRequests) {
            return false;
        }

        // 3. 记录本次请求
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

        // 4. 设置过期时间，防止冷用户的 key 永远留在 Redis
        redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);

        return true;
    }
}