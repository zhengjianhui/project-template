package com.yzf.project.projectservice.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.yzf.project.projectservice.config.env.SystemProperties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhengjianhui on 18/3/16
 */
@Configuration
@AutoConfigureAfter(DruidDataSourceConfig.class)
public class DruidDataSourceConfig {

    @Autowired
    private SystemProperties systemProperties;

    @Bean(name = "druidDataSource")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        // mysql 驱动
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        // url
        dataSource.setUrl(systemProperties.getValueByKey("jdbc.url"));
        // 账号
        dataSource.setUsername(systemProperties.getValueByKey("jdbc.username"));
        // 密码
        dataSource.setPassword(systemProperties.getValueByKey("jdbc.password"));
        // 初始化连接
        dataSource.setInitialSize(Integer.parseInt(systemProperties.getValueByKey("jdbc.initialSize")));
        // 最大线程数
        dataSource.setMaxActive(Integer.parseInt(systemProperties.getValueByKey("jdbc.maxActive")));
        // 空闲时, 最大保留连接数
        dataSource.setMinIdle(Integer.parseInt(systemProperties.getValueByKey("jdbc.minIdle")));
        // 获取连接等待时长
        dataSource.setMaxWait(Integer.parseInt(systemProperties.getValueByKey("jdbc.maxWait")));

        // 试探sql
        dataSource.setValidationQuery(systemProperties.getValueByKey("jdbc.validationQuery"));
        // 默认开启, 设置一发
        dataSource.setTestWhileIdle(true);
        // 心跳检查
        dataSource.setKeepAlive(Boolean.parseBoolean(systemProperties.getValueByKey("jdbc.keepAlive")));

        // 后台检查线程的休眠时间 以及 idleMillis 空闲时间检查连接是否有效的配置
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(systemProperties.getValueByKey("jdbc.timeBetweenEvictionRunsMillis")));
        // 连接保持空闲而不被驱逐的最长时间
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(systemProperties.getValueByKey("jdbc.minEvictableIdleTimeMillis")));
        dataSource.setMaxEvictableIdleTimeMillis(Long.parseLong(systemProperties.getValueByKey("jdbc.maxEvictableIdleTimeMillis")));




        return dataSource;
    }

}
