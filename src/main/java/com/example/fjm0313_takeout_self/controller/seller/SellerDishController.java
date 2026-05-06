package com.example.fjm0313_takeout_self.controller.seller;


import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.entity.Dish;
import com.example.fjm0313_takeout_self.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("seller/dish")
public class SellerDishController {

    @Autowired
    private DishService dishService;

    @LoginRequired("EMPLOYEE")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Dish dish){
        dishService.addDish(dish);
        return Result.success("保存成功");
    }

    @LoginRequired("EMPLOYEE")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam List<Long> ids){
        dishService.deleteDishByIds(ids);
        return Result.success("删除成功");
    }


    @LoginRequired("EMPLOYEE")
    @GetMapping("/list")
    public Result<List<Dish>> list(){
        List<Dish> dishes = dishService.findAll();
        return Result.success(dishes);
    }

    @LoginRequired("EMPLOYEE")
    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id){
        Dish dish = dishService.findById(id);
        return Result.success(dish);
    }

    @LoginRequired("EMPLOYEE")
    @PostMapping("/update")
    public Result<String> update(@RequestBody Dish dish){
        dishService.updateDish(dish);
        return Result.success("修改成功");
    }

}

