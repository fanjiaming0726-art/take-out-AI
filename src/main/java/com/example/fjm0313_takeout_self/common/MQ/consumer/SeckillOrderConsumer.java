package com.example.fjm0313_takeout_self.common.MQ.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.common.MQ.message.SeckillMessage;
import com.example.fjm0313_takeout_self.common.MQ.sender.OrderTimeoutSender;
import com.example.fjm0313_takeout_self.config.RabbitMQConfig;
import com.example.fjm0313_takeout_self.entity.AddressBook;
import com.example.fjm0313_takeout_self.entity.SeckillActivity;
import com.example.fjm0313_takeout_self.entity.SeckillOrder;
import com.example.fjm0313_takeout_self.entity.User;
import com.example.fjm0313_takeout_self.mapper.AddressBookMapper;
import com.example.fjm0313_takeout_self.mapper.SeckillActivityMapper;
import com.example.fjm0313_takeout_self.mapper.SeckillOrderMapper;
import com.example.fjm0313_takeout_self.mapper.UserMapper;
import com.example.fjm0313_takeout_self.service.RankingService;
import com.example.fjm0313_takeout_self.service.SeckillService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SeckillOrderConsumer {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private OrderTimeoutSender orderTimeoutSender;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    @Transactional
    public void handleSeckillOrder(SeckillMessage message) {
        try {
            Long activityId = message.getActivityId();
            Long userId = message.getUserId();

            // 拿取秒杀活动
            SeckillActivity activity = seckillService.findById(activityId);


            // 2. 扣秒杀活动的库存
            seckillActivityMapper.deductStock(activityId);

            // 3. 查用户和默认地址
            User user = userMapper.selectById(userId);
            AddressBook addressBook = addressBookMapper.selectOne(
                    new LambdaQueryWrapper<AddressBook>()
                            .eq(AddressBook::getUserId, userId)
                            .eq(AddressBook::getIsDefault, 1)
            );

            // 4. 创建秒杀订单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setUserId(userId);
            seckillOrder.setUsername(user.getUsername());
            seckillOrder.setActivityId(activityId);
            seckillOrder.setDishId(activity.getDishId());
            seckillOrder.setDishName(activity.getDishName());
            seckillOrder.setSeckillPrice(activity.getSeckillPrice());
            seckillOrder.setOrderNumber(UUID.randomUUID().toString().replace("-", ""));
            seckillOrder.setStatus(0);  // 改为未支付，之前是1

            if (addressBook != null) {
                seckillOrder.setConsignee(addressBook.getConsignee());
                seckillOrder.setPhone(addressBook.getPhone());
                String fullAddress = (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                        + (addressBook.getDetail() == null ? "" : addressBook.getDetail());
                seckillOrder.setAddress(fullAddress);
            }

            seckillOrderMapper.insert(seckillOrder);

            // 发送延迟消息
            orderTimeoutSender.sendOrderTimeoutMessage(seckillOrder.getId(), "SECKILL");
            // 5. 更新排行榜
            rankingService.increaseSales(activity.getDishId(), activity.getDishName(), 1);

            System.out.println("秒杀订单创建成功，userId=" + userId + ", activityId=" + activityId);
        } catch (Exception e) {
            System.out.println("秒杀订单处理失败：" + e.getMessage());
        }
    }
}