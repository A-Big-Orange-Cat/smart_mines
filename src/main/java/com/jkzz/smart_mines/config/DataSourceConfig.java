package com.jkzz.smart_mines.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.datasoursename}")
    private String database;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl(getURL());
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 可以设置更多的配置属性，例如连接池大小，最大生命周期等
        return dataSource;
    }

    private String getURL() {
        String path = "jdbc:h2:" + System.getProperty("user.dir").replaceAll("\\\\", "/") + "/Application/h2/" + database;
        try {
            return URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        //return "jdbc:mysql://127.0.0.1:" + 3306 + "/" + database + "?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8";
    }

}
