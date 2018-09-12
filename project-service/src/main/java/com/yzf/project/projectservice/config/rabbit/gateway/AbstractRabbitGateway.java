package com.yzf.project.projectservice.config.rabbit.gateway;


import com.yzf.project.projectservice.config.rabbit.message.RabbitMessage;
import org.springframework.amqp.rabbit.core.RabbitGatewaySupport;

/**
 * @author zhengjianhui on 18/3/16
 */
public abstract class AbstractRabbitGateway extends RabbitGatewaySupport {
    /**
     * 发送消息 [参数列表，说明每个参数用途]
     *
     * @param queue         队列名称
     * @param rabbitMessage 消息对象
     * @return void
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract void send(String queue, RabbitMessage rabbitMessage);


    /**
     * 发送广播消息 [参数列表，说明每个参数用途]
     *
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     *
     * @param exchange
     * @param rabbitMessage
     * @return void
     */
    public abstract void sendFanoutExchange(String exchange, RabbitMessage rabbitMessage);

    /**
     * 支持事务的广播消息
     * @param exchange
     * @param rabbitMessage
     * @return void
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract void sendFanoutExchangeWithTransactionAware(String exchange, RabbitMessage rabbitMessage);

    /**
     * 消息支持事务，如果事务回滚则不发消息，没有事务，或事务提交成功发送消息
     * @param queue
     * @param msg
     * @return void
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract void sendWithTransactionAware(final String queue, final RabbitMessage msg);

    /**
     * Description: <br> 同步消息，带返回值，默认超时时间为5秒 Implement: <br> 1、… <br> 2、… <br> [参数列表，说明每个参数用途]
     * @param queue
     * @param msg
     * @return Object
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract Object sendAndReceive(final String queue, final RabbitMessage msg);

}