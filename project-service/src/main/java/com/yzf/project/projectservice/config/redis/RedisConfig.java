package com.yzf.project.projectservice.config.redis;

import com.yzf.project.projectservice.config.env.SystemProperties;
import com.yzf.project.projectservice.config.redis.serializer.HessianSerializationRedisSerializer;
import com.yzf.project.projectservice.config.redis.template.HessianRedisTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhengjianhui on 18/3/16
 */
@Configuration
public class RedisConfig {

    @Autowired
    private SystemProperties systemProperties;

    @Bean
    public JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder JedisPoolingClientConfigurationBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration
                .builder();

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();

        String maxTotal = systemProperties.getValueByKey("redis.pool.maxTotal");
        if (StringUtils.isNotBlank(maxTotal)) {
            genericObjectPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
        }

        String maxIdle = systemProperties.getValueByKey("redis.pool.maxIdle");
        if (StringUtils.isNotBlank(maxIdle)) {
            genericObjectPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
        }

        String minIdle = systemProperties.getValueByKey("redis.pool.minIdle");
        if (StringUtils.isNotBlank(minIdle)) {
            genericObjectPoolConfig.setMinIdle(Integer.parseInt(minIdle));
        }

        String maxWaitMillis = systemProperties.getValueByKey("redis.pool.maxWaitMillis");
        if (StringUtils.isNotBlank(maxWaitMillis)) {
            genericObjectPoolConfig.setMaxWaitMillis(Long.parseLong(maxWaitMillis));
        }

        String testOnBorrow = systemProperties.getValueByKey("redis.pool.testOnBorrow");
        if (StringUtils.isNotBlank(testOnBorrow)) {
            genericObjectPoolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
        }

        return JedisPoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig).build();
    }

    @Bean
    public RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
        RedisStandaloneConfiguration jedisClientConfiguration = new RedisStandaloneConfiguration();
        jedisClientConfiguration.setDatabase(0);
        jedisClientConfiguration.setHostName(systemProperties.getValueByKey("redis.connection.hostName"));
        jedisClientConfiguration.setPort(Integer.parseInt(systemProperties.getValueByKey("redis.connection.port")));

        return jedisClientConfiguration;
    }

    @Bean
    public JedisConnectionFactory getConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(getRedisStandaloneConfiguration(), getJedisClientConfiguration());

        return factory;
    }

    @Bean
    public RedisTemplate getRedisTemplate() {
        HessianRedisTemplate template = new HessianRedisTemplate();
        template.setConnectionFactory(getConnectionFactory());

        HessianSerializationRedisSerializer serializer = new HessianSerializationRedisSerializer();
        serializer.setEnableGzip(Boolean.TRUE);

        template.setHessianSerializationRedisSerializer(serializer);
        return template;
    }

}
