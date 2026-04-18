package com.example.fjm0313_takeout_self.service;

import com.example.fjm0313_takeout_self.entity.Dish;

import java.util.List;

public interface DishService {
    void restoreStock(Long dishId, int count);
    void addDish(Dish dish);
    void deleteDishByIds(List<Long> ids);
    List<Dish> findAll();
    Dish findById(Long id);
    void updateDish(Dish dish);
    void deductStock(Long dishId, int count);
}