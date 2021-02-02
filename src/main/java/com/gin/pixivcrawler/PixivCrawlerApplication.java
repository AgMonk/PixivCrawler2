package com.gin.pixivcrawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan(value = {"com.gin.pixivcrawler.dao"})
public class PixivCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixivCrawlerApplication.class, args);
    }

}
