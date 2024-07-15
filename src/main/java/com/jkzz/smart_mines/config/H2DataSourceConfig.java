package com.jkzz.smart_mines.config;

import com.jkzz.smart_mines.communication.manager.CommunicationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.net.URLDecoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class H2DataSourceConfig {

    private final DataSource dataSource;
    private final ApplicationContext applicationContext;
    private final CommunicationManager communicationManager;
    // 初始化sql
    @Value("${spring.datasource.datasource_name}")
    private String schema;

    @PostConstruct
    public void init() throws Exception {
        // 创建一个标识文件,只有在第一次初始化数据库时会创建,如果程序目录下有这个文件,就不会重新执行sql脚本
        File f = new File(System.getProperty("user.dir") + File.separator + "Application\\h2\\smart_mines.lock");
        // 初始化本地数据库
        if (!f.exists()) {
            log.info("------------------------------初始化h2数据------------------------------");
            if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) {
                log.error("创建h2标识文件smart_mines.lock父目录失败");
            }
            if (!f.createNewFile()) {
                log.error("创建h2标识文件smart_mines.lock失败");
            }
            // 加载资源文件
            Resource resource = applicationContext.getResource("classpath:db/" + schema + ".sql");
            log.info("数据库初始化路径{}", URLDecoder.decode(resource.getURL().getPath(), "utf-8"));
            // 手动执行SQL语句
            ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        }
        // 初始化PLC通讯
        communicationManager.init();
    }

}
