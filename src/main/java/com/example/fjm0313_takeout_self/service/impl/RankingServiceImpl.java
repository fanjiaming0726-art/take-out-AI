package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RANKING_KEY = "dish:sales:ranking";

    @Override
    public void increaseSales(Long dishId, String dishName, int count) {
        // ZINCRBY：给指定菜品的销量加 count
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, dishId + ":" + dishName, count);
    }

    @Override
    public List<Map<String, Object>> getTopN(int n) {
        // ZREVRANGE：按分数从高到低取前 N 个
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, 0, n - 1);

        if (tuples == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            String value = tuple.getValue().toString();
            // value 格式是 "dishId:dishName"
            String[] parts = value.split(":");

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", rank++);
            item.put("dishId", Long.valueOf(parts[0]));
            item.put("dishName", parts[1]);
            item.put("sales", tuple.getScore().intValue());
            result.add(item);
        }
        return result;
    }
}