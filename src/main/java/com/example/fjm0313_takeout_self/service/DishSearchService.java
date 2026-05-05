package com.example.fjm0313_takeout_self.service;

import com.example.fjm0313_takeout_self.es.DishDoc;

import java.util.List;

public interface DishSearchService {

    void saveDishToEs(Long dishId);

    void deleteDishFromEs(Long dishId);

    void rebuildDishIndex();

    List<DishDoc> search(String keyword);
}