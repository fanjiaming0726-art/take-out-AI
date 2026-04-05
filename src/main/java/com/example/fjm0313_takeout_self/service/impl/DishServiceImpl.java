package com.example.fjm0313_takeout_self.service.impl;

import com.example.fjm0313_takeout_self.entity.Dish;
import com.example.fjm0313_takeout_self.mapper.DishMapper;
import com.example.fjm0313_takeout_self.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;


    @Override
    public void addDish(Dish dish) {
        dishMapper.insert(dish);
    }

    @Override
    public void deleteDishByIds(List<Long> ids) {
        dishMapper.deleteBatchIds(ids);
    }

    @Override
    public List<Dish> findAll() {
        return dishMapper.selectList(null);
    }

    @Override
    public Dish findById(Long id) {
       return dishMapper.selectById(id);
    }

    @Override
    public void updateDish(Dish dish) {
        dishMapper.updateById(dish);
    }

    @Override
    public void deductStock(Long dishId, int count) {
        Dish dish = dishMapper.selectById(dishId);
        if(dish == null){
            throw new RuntimeException("菜品不存在");
        }
        if(dish.getStock() < count){
            throw new RuntimeException(dish.getName() + "库存不足，剩余：" + dish.getStock());
        }

        int rows = dishMapper.deductStock(dishId,count,dish.getVersion());

        if(rows == 0){
            dish = dishMapper.selectById(dishId);

            if(dish.getStock() < count){
                throw new RuntimeException(dish.getName() + "库存不足，剩余：" + dish.getStock());
            }

            rows = dishMapper.deductStock(dishId,count,dish.getVersion());
            if(rows == 0){
                throw new RuntimeException("系统繁忙，请稍后再试");
            }
        }
    }
}