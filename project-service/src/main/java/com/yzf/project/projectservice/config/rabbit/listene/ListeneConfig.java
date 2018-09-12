package com.yzf.project.projectservice.config.rabbit.listene;

import com.yzf.project.projectservice.config.env.SystemProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhengjianhui on 18/3/16
 */
@Configuration
@EnableRabbit
public class ListeneConfig {

    @Autowired
    private SystemProperties systemProperties;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 此处使用 Jackson2JsonMessageConverter 能保证对象序列化回来, 否则可能会有序列化问题(比如类全名)
        factory.setMessageConverter(new Jackson2JsonMessageConverter());

        Integer maxConcurrent = Integer.valueOf(systemProperties.getValueByKey("rabbitmq.consumers.maxConcurrent"));
        Integer concurrent = Integer.valueOf(systemProperties.getValueByKey("rabbitmq.consumers.concurrent"));

        // 最大并发消费
        factory.setMaxConcurrentConsumers(maxConcurrent == null ? 50 : maxConcurrent);
        // 最小并发消费
        factory.setConcurrentConsumers(concurrent == null ? 10 : concurrent);

        // 设置每次消费消息数
//        factory.setPrefetchCount(10);
        return factory;
    }
}
