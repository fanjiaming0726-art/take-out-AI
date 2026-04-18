package com.example.fjm0313_takeout_self.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fjm0313_takeout_self.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    @Update("UPDATE dish SET stock = stock - #{count}, version = version + 1 " +
            "WHERE id = #{dishId} AND stock >= #{count} AND version = #{version}")
    int deductStock(@Param("dishId") Long dishId, @Param("count") int count, @Param("version") int version);

    @Update("UPDATE dish SET stock = stock + #{count} WHERE id = #{dishId}")
    int restoreStock(@Param("dishId") Long dishId, @Param("count") int count);

}
