package com.example.fjm0313_takeout_self.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fjm0313_takeout_self.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    @Update("UPDATE seckill_activity SET total_stock = total_stock - 1 " +
            "WHERE id = #{activityId} AND total_stock > 0")
    int deductStock(@Param("activityId") Long activityId);
}