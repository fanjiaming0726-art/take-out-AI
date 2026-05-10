package com.example.fjm0313_takeout_self.controller.seller;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.fjm0313_takeout_self.entity.SeckillActivity;
import com.example.fjm0313_takeout_self.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/seckill")
public class SellerSeckillController {

    @Autowired
    private SeckillService seckillService;

    @LoginRequired("EMPLOYEE")
    @GetMapping("/list")
    public Result<List<SeckillActivity>> list() {
        return Result.success(seckillService.listActivities());
    }



    @LoginRequired("EMPLOYEE")
    @PostMapping("/create")
    public Result<String> create(@RequestBody SeckillActivity activity) {
        try {
            seckillService.createActivity(activity);
            return Result.success("秒杀活动创建成功，库存已从菜品划拨");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}