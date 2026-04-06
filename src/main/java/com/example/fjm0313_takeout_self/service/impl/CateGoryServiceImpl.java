package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.entity.Category;
import com.example.fjm0313_takeout_self.mapper.CateGoryMapper;
import com.example.fjm0313_takeout_self.service.CateGoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CateGoryServiceImpl implements CateGoryService {

    @Autowired
    private CateGoryMapper cateGoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CATEGORY_LIST_KEY = "category:list";

    @Override
    public void addCategory(Category category) {
        cateGoryMapper.insert(category);
        // 新增后删除缓存，下次查询重新加载
        redisTemplate.delete(CATEGORY_LIST_KEY);
    }

    @Override
    public void updateCategory(Category category) {
        cateGoryMapper.updateById(category);
        redisTemplate.delete(CATEGORY_LIST_KEY);
    }

    @Override
    public Category findById(Long id) {
        return cateGoryMapper.selectById(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> findAll() {
        // 1. 先查缓存
        List<Category> list = (List<Category>) redisTemplate.opsForValue().get(CATEGORY_LIST_KEY);
        if (list != null) {
            return list;
        }
        // 2. 缓存没有，查数据库
        list = cateGoryMapper.selectList(null);
        // 3. 存入缓存，过期时间 30 分钟
        redisTemplate.opsForValue().set(CATEGORY_LIST_KEY, list, 30, TimeUnit.MINUTES);
        return list;
    }
}