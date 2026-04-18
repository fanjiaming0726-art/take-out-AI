package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.entity.Dish;
import com.example.fjm0313_takeout_self.mapper.DishMapper;
import com.example.fjm0313_takeout_self.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String DISH_LIST_KEY = "dish:list";

    @Override
    public void addDish(Dish dish) {
        dishMapper.insert(dish);
        redisTemplate.delete(DISH_LIST_KEY);
    }

    @Override
    public void deleteDishByIds(List<Long> ids) {
        dishMapper.deleteBatchIds(ids);
        redisTemplate.delete(DISH_LIST_KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Dish> findAll() {
        // 1. 先查缓存
        List<Dish> list = (List<Dish>) redisTemplate.opsForValue().get(DISH_LIST_KEY);
        if (list != null) {
            return list;
        }
        // 2. 缓存没有，查数据库
        list = dishMapper.selectList(null);
        // 3. 存入缓存，过期时间 30 分钟
        redisTemplate.opsForValue().set(DISH_LIST_KEY, list, 30, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public Dish findById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public void updateDish(Dish dish) {
        dishMapper.updateById(dish);
        redisTemplate.delete(DISH_LIST_KEY);
    }

    @Override
    public void deductStock(Long dishId, int count) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (dish.getStock() < count) {
            throw new RuntimeException(dish.getName() + " 库存不足，剩余：" + dish.getStock());
        }
        int rows = dishMapper.deductStock(dishId, count, dish.getVersion());
        if (rows == 0) {
            dish = dishMapper.selectById(dishId);
            if (dish.getStock() < count) {
                throw new RuntimeException(dish.getName() + " 库存不足，剩余：" + dish.getStock());
            }
            rows = dishMapper.deductStock(dishId, count, dish.getVersion());
            if (rows == 0) {
                throw new RuntimeException("系统繁忙，请重试");
            }
        }
        // 库存变了，删除缓存
        redisTemplate.delete(DISH_LIST_KEY);
    }


    @Override
    public void restoreStock(Long dishId, int count) {
        dishMapper.restoreStock(dishId, count);
        redisTemplate.delete(DISH_LIST_KEY);
    }
}