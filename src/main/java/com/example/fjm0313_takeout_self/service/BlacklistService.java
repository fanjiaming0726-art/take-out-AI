package com.example.fjm0313_takeout_self.service;

import java.util.Set;

public interface BlacklistService {
    void addToBlacklist(Long userId, String reason);
    void removeFromBlacklist(Long userId);
    boolean isBlacklisted(Long userId);
    Set<String> getAllBlacklist();
}