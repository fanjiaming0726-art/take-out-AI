package com.example.fjm0313_takeout_self.common.MQ.message;

import lombok.Data;
import java.io.Serializable;

@Data
public class SeckillMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long activityId;
    private Long userId;
}