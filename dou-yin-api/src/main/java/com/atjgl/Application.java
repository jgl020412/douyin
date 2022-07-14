package com.atjgl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 小亮
 * 启动类，用于整个软件的启动
 **/

@SpringBootApplication
@MapperScan(basePackages = "com.atjgl.mapper")
@ComponentScan(basePackages = {"com.atjgl", "org.n3r.idworker"})
@EnableMongoRepositories
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
