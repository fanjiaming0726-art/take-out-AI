package com.example.fjm0313_takeout_self;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 开启事务注解。因为接口的有些操作是需要同成功同失败的，在那个接口上面加上@Transaction注解后，在启动类加一个@EnableTransactionManagement来启动事务就行了
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
public class Fjm0313TakeoutSelfApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fjm0313TakeoutSelfApplication.class, args);
    }

}
