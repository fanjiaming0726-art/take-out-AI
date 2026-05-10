package com.example.fjm0313_takeout_self.controller.customer;

import com.example.common.annotation.LoginRequired;
import com.example.fjm0313_takeout_self.common.MQ.message.SeckillMessage;
import com.example.fjm0313_takeout_self.common.MQ.sender.SeckillOrderSender;
import com.example.common.result.Result;
import com.example.common.context.UserContext;
import com.example.fjm0313_takeout_self.entity.AddressBook;
import com.example.fjm0313_takeout_self.entity.SeckillActivity;
import com.example.fjm0313_takeout_self.service.AddressBookService;
import com.example.fjm0313_takeout_self.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/seckill")
public class CustomerSeckillController {

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SeckillOrderSender seckillOrderSender;


    @LoginRequired("CUSTOMER")
    @GetMapping("/list")
    public Result<List<SeckillActivity>> list() {
        return Result.success(seckillService.listActivities());
    }

    @LoginRequired("CUSTOMER")
    @PostMapping("/rush/{activityId}")
    public Result<String> rush(@PathVariable Long activityId) {
        Long userId = UserContext.getUserId();

        AddressBook addressBook = addressBookService.findDefaultByUserId(userId);
        if(addressBook == null){
            return Result.fail("还没有填写地址哦");
        }

        int result = seckillService.trySeckill(activityId, userId);

        if (result == 0) {
            // 秒杀成功，发消息到 MQ，异步创建订单
            SeckillMessage message = new SeckillMessage();
            message.setActivityId(activityId);
            message.setUserId(userId);
            seckillOrderSender.sendSeckillOrder(message);
            return Result.success("秒杀成功，订单生成中，请稍后查看");
        }

        return switch (result) {
            // case 0 -> Result.success("秒杀成功，请尽快下单");
            case -1 -> Result.fail("很遗憾，已被抢光了");
            case -2 -> Result.fail("您已经秒杀过了，不能重复参与");
            default -> Result.fail("系统繁忙，请稍后再试");
        };
    }
}