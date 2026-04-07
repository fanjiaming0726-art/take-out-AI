package com.example.fjm0313_takeout_self.service;

public interface RateLimitService {
    boolean isAllowed(Long userId, int maxRequests, int windowSeconds);
}