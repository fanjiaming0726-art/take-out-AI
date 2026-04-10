package com.example.fjm0313_takeout_self.controller.seller;

import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
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
    @PostMapping("/load/{id}")
    public Result<String> load(@PathVariable Long id) {
        try {
            seckillService.loadActivityToRedis(id);
            return Result.success("活动库存已加载到 Redis");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}