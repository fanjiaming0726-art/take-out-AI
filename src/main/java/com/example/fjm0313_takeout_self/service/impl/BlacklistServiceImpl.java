package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_KEY = "user:blacklist";

    @Override
    public void addToBlacklist(Long userId, String reason) {
        // 用 Set 结构存储黑名单用户ID
        redisTemplate.opsForSet().add(BLACKLIST_KEY, userId.toString());
        // 单独存一个 key 记录拉黑原因
        redisTemplate.opsForValue().set("user:blacklist:reason:" + userId, reason);
    }

    @Override
    public void removeFromBlacklist(Long userId) {
        redisTemplate.opsForSet().remove(BLACKLIST_KEY, userId.toString());
        redisTemplate.delete("user:blacklist:reason:" + userId);
    }

    @Override
    public boolean isBlacklisted(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, userId.toString()));
    }

    @Override
    public Set<String> getAllBlacklist() {
        Object a = new Object();
        @SuppressWarnings("unchecked")
        Set<String> members = (Set<String>) (Set<?>) redisTemplate.opsForSet().members(BLACKLIST_KEY);
        return members;
    }
}