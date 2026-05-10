package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.fjm0313_takeout_self.es.DishDoc;
import com.example.fjm0313_takeout_self.service.DishSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/dish/search")
public class CustomerDishSearchController {

    @Autowired
    private DishSearchService dishSearchService;

    @LoginRequired("CUSTOMER")
    @GetMapping
    public Result<List<DishDoc>> search(@RequestParam String keyword) {
        return Result.success(dishSearchService.search(keyword));
    }
}