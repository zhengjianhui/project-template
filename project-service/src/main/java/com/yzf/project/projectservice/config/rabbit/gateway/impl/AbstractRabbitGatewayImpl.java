package com.yzf.project.projectservice.config.rabbit.gateway.impl;

import com.yzf.project.projectservice.config.rabbit.gateway.AbstractRabbitGateway;
import com.yzf.project.projectservice.config.rabbit.message.RabbitMessage;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author zhengjianhui on 18/3/16
 */
public class AbstractRabbitGatewayImpl extends AbstractRabbitGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRabbitGatewayImpl.class);

    @Override
    public void send(final String queue, final RabbitMessage msg) {

        LOGGER.info("send begin send queue:{},msg:{}", queue, msg);
        msg.setMessageId(UUID.randomUUID().toString());
        this.getRabbitOperations().convertAndSend(queue, msg, (message) -> {

            try {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            } catch (Exception e) {
                LOGGER.error("send message error:", e);
                throw new AmqpException(e);
            }
            return message;
        });
        LOGGER.info("send end send queue:{},msg:{}", queue, msg);
    }

    @Override
    public Object sendAndReceive(final String queue, final RabbitMessage msg) {
        LOGGER.info("sendAndReceive begin send queue:{},msg:{}", queue, msg);
        Object ret = this.getRabbitOperations().convertSendAndReceive(queue, msg, (message) -> {
            try {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            } catch (Exception e) {
                LOGGER.error("send message error:", e);
                throw new AmqpException(e);
            }
            return message;
        });
        LOGGER.info("sendAndReceive end send queue:{},msg:{}", queue, msg);
        return ret;
    }

    @Override
    public void sendWithTransactionAware(final String queue, final RabbitMessage msg) {

        // 判断当前线程事务是否开启
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    LOGGER.info("sendWithTransactionAware begin send queue:{},msg:{}", queue, msg);
                    send(queue, msg);
                    LOGGER.info("sendWithTransactionAware end send queue:{},msg:{}", queue, msg);
                }
            });
        } else {
            LOGGER.info("sendWithTransactionAware noneTransaction begin  send queue:{},msg:{}", queue, msg);
            send(queue, msg);
            LOGGER.info("sendWithTransactionAware noneTransaction end send queue:{},msg:{}", queue, msg);
        }
    }

    @Override
    public void sendFanoutExchange(String exchange, RabbitMessage msg) {

        LOGGER.info("sendFanoutExchange begin  send exchange:{},msg:{}", exchange, msg);
        this.getRabbitOperations().convertAndSend(exchange, "", msg, (message) -> {
            try {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            } catch (Exception e) {
                LOGGER.error("send message error:", e);
                throw new AmqpException(e);
            }
            return message;
        });
        LOGGER.info("sendFanoutExchange begin  send exchange:{},msg:{}", exchange, msg);
    }

    @Override
    public void sendFanoutExchangeWithTransactionAware(final String exchange, final RabbitMessage msg) {

        // 判断当前线程事务是否开启
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

                @Override
                public void afterCommit() {

                    LOGGER.info("sendFanoutExchangeWithTransactionAware begin send exchange:{},msg:{}", exchange, msg);
                    sendFanoutExchange(exchange, msg);
                    LOGGER.info("sendFanoutExchangeWithTransactionAware end send exchange:{},msg:{}", exchange, msg);

                }
            });
        } else {
            LOGGER.info("sendFanoutExchangeWithTransactionAware begin send exchange:{},msg:{}", exchange, msg);
            sendFanoutExchange(exchange, msg);
            LOGGER.info("sendFanoutExchangeWithTransactionAware end send exchange:{},msg:{}", exchange, msg);
        }

    }
}
