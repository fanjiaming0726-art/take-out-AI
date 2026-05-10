package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.fjm0313_takeout_self.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer/ranking")
public class CustomerRankingController {

    @Autowired
    private RankingService rankingService;

    @LoginRequired("CUSTOMER")
    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> hot() {
        // 用户端默认展示前 5 名
        return Result.success(rankingService.getTopN(5));
    }
}