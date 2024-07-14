package com.jkzz.smart_mines;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @Description: SpringBoot启动类
 * @Author: 孙志东
 * @DateTime: 2024/7/14 下午7:56
 **/
@SpringBootApplication
@MapperScan(basePackages="com.jkzz.smart_mines.mapper")
public class SmartMinesApplication {
    /**
     * @Description: 启动类方法
     * @Author: 孙志东
     * @DateTime: 2024/7/14 下午7:58
     * @Params: [args]
     * @Return: void
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SmartMinesApplication.class, args);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 启动成功! >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Environment env = applicationContext.getEnvironment();
        System.out.println("本地接口地址------http://127.0.0.1:" + env.getProperty("server.port"));
        System.out.println("测试接口地址------http://127.0.0.1:" + env.getProperty("server.port") + "/swagger-ui/index.html");
        System.out.println("接口文档地址------http://127.0.0.1:" + env.getProperty("server.port") + "/doc.html");
    }
}
