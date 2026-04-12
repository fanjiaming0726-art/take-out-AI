package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.entity.SeckillActivity;
import com.example.fjm0313_takeout_self.mapper.SeckillActivityMapper;
import com.example.fjm0313_takeout_self.service.DishService;
import com.example.fjm0313_takeout_self.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    // Long指定Lua执行器的返回值类型
    private DefaultRedisScript<Long> seckillScript;

    @Autowired
    private DishService dishService;

    @PostConstruct
    public void init() {
        // 创建新的Lua脚本执行器
        seckillScript = new DefaultRedisScript<>();

        // 指定Lua脚本的位置
        seckillScript.setLocation(new ClassPathResource("seckill.lua"));

        // 设定lua脚本的返回值类型
        seckillScript.setResultType(Long.class);
    }

    @Override
    public void loadActivityToRedis(Long activityId) {
        SeckillActivity activity = seckillActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("秒杀活动不存在");
        }
        // 把库存加载到 Redis
        String stockKey = "seckill:stock:" + activityId;
        redisTemplate.opsForValue().set(stockKey, activity.getTotalStock());
    }

    @Override
    public int trySeckill(Long activityId, Long userId) {
        String stockKey = "seckill:stock:" + activityId;
        String usersKey = "seckill:users:" + activityId;

        // 执行 Lua 脚本
        Long result = redisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, usersKey), // KEYS
                userId.toString() // ARGV参数
        );

        return result == null ? -3 : result.intValue();
    }

    @Override
    public List<SeckillActivity> listActivities() {
        return seckillActivityMapper.selectList(null);
    }

    @Override
    public SeckillActivity findById(Long id) {
        return seckillActivityMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createActivity(SeckillActivity activity) {
        // 1. 从 dish 表扣除库存（划拨到秒杀活动）
        dishService.deductStock(activity.getDishId(), activity.getTotalStock());

        // 2. 保存秒杀活动
        activity.setStatus(0); // 未开始
        seckillActivityMapper.insert(activity);
    }
}