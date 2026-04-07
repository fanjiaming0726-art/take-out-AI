package com.example.fjm0313_takeout_self.service;

import java.util.List;
import java.util.Map;

public interface RankingService {
    void increaseSales(Long dishId, String dishName, int count);
    List<Map<String, Object>> getTopN(int n);
}