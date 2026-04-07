package com.example.fjm0313_takeout_self.controller.seller;

import com.example.fjm0313_takeout_self.common.LoginRequired;
import com.example.fjm0313_takeout_self.common.Result;
import com.example.fjm0313_takeout_self.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller/ranking")
public class SellerRankingController {

    @Autowired
    private RankingService rankingService;

    @LoginRequired("EMPLOYEE")
    @GetMapping("/top")
    public Result<List<Map<String, Object>>> top(@RequestParam(defaultValue = "10") int n) {
        return Result.success(rankingService.getTopN(n));
    }
}