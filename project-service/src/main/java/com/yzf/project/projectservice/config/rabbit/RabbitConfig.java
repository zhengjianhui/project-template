package com.yzf.project.projectservice.config.rabbit;

import com.yzf.project.projectservice.config.env.SystemProperties;
import com.yzf.project.projectservice.config.rabbit.gateway.AbstractRabbitGateway;
import com.yzf.project.projectservice.config.rabbit.gateway.impl.AbstractRabbitGatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhengjianhui on 18/3/16
 */
@Configuration
public class RabbitConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRabbitGatewayImpl.class);

    /**
     * 持久化配置
     */
//    public static final Boolean PERSISTENCE = Boolean.TRUE;

    @Autowired
    private SystemProperties systemProperties;

    /**
     * 连接工厂基本配置
     *
     * @return ConnectionFactory 连接工厂
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(systemProperties.getValueByKey("rabbitmq.host"));
        factory.setPort(Integer.parseInt(systemProperties.getValueByKey("rabbitmq.port")));
        factory.setUsername(systemProperties.getValueByKey("rabbitmq.username"));
        factory.setPassword(systemProperties.getValueByKey("rabbitmq.password"));

        // 开启手动 ack
        factory.setPublisherConfirms(true);

        return factory;

    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 模板类
     *
     * @return RabbitTemplate rabbitmq模板
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // 开启回调
        template.setMandatory(true);
        // 用 json 的方式处理消息
        template.setMessageConverter(jsonMessageConverter());

        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationIdString();
            LOGGER.info("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
        });
        //        消息确认  yml 需要配置   publisher-returns: true
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                LOGGER.info("消息发送到exchange成功,id: {}", correlationData);
            } else {
                LOGGER.info("消息发送到exchange失败,原因: {}", cause);
            }
        });
        return template;
    }

    @Bean
    public AbstractRabbitGateway rabbitGateway() {
        AbstractRabbitGateway gateway = new AbstractRabbitGatewayImpl();
        gateway.setRabbitOperations(rabbitTemplate());

        return gateway;
    }


//    /**
//     * 在创建了多个ConnectionFactory时，必须定义RabbitAdmin，否则无法自动创建exchange,queue
//     *
//     * public RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory")ConnectionFactory connectionFactory)
//     * @param connectionFactory
//     * @return
//     */
//    @Bean
//    public RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory")ConnectionFactory connectionFactory){
//        return new RabbitAdmin(connectionFactory);
//    }
}
