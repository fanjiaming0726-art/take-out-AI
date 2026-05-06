package com.example.fjm0313_takeout_self.controller.seller;

import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.entity.Category;
import com.example.fjm0313_takeout_self.service.CateGoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/category")
public class SellerCategoryController {


    @Autowired
    private CateGoryService cateGoryService;

    @LoginRequired("EMPLOYEE")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Category category){
        cateGoryService.addCategory(category);
        return Result.success("保存成功");
    }

    @LoginRequired("EMPLOYEE")
    @PostMapping("/update")
    public Result<String> update(@RequestBody Category category){
        cateGoryService.updateCategory(category);
        return Result.success("更新成功");
    }

    @LoginRequired("EMPLOYEE")
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id){
        Category category = cateGoryService.findById(id);
        return Result.success(category);
    }

    @LoginRequired("EMPLOYEE")
    @GetMapping("/list")
    public Result<List<Category>> list(){
        List<Category> list = cateGoryService.findAll();
        return Result.success(list);
    }
}
