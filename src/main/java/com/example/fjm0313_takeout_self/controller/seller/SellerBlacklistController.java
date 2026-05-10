package com.example.fjm0313_takeout_self.controller.seller;

import com.example.common.annotation.LoginRequired;
import com.example.common.result.Result;
import com.example.fjm0313_takeout_self.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/seller/blacklist")
public class SellerBlacklistController {

    @Autowired
    private BlacklistService blacklistService;

    @LoginRequired("EMPLOYEE")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String reason = (String) params.get("reason");
        blacklistService.addToBlacklist(userId, reason);
        return Result.success("已将用户加入黑名单");
    }

    @LoginRequired("EMPLOYEE")
    @PostMapping("/remove")
    public Result<String> remove(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        blacklistService.removeFromBlacklist(userId);
        return Result.success("已将用户移出黑名单");
    }

    @LoginRequired("EMPLOYEE")
    @GetMapping("/list")
    public Result<Set<String>> list() {
        return Result.success(blacklistService.getAllBlacklist());
    }
}