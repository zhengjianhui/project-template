package com.yzf.project.projectservice.config.rabbit.message;


import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengjianhui on 18/3/16
 */
public class RabbitMessage implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2297427939937542841L;

    private String messageId;

    public String getMessageId() {

        return messageId;
    }

    public void setMessageId(String messageId) {

        this.messageId = messageId;
    }

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 处理时间
     */
    private Date processingTime;

    /**
     * 超时时间
     */
    private long timeout = -1;

    /**
     * 消息体
     */
    private Object body;

    /**
     * 下个处理队列
     */
    private String sendTo;

    /**
     * 发送节点
     */
    private String sendNode;

    /**
     * 处理节点
     */
    private String processNode;

    public RabbitMessage()
    {
        this.createTime = new Date();
    }

    public RabbitMessage(Object body)
    {
        this.createTime = new Date();
        this.body = body;
    }

    public RabbitMessage(Object body, long timeout)
    {
        this.createTime = new Date();
        this.body = body;
        this.timeout = timeout;
    }

    public RabbitMessage(Object body, String sendTo)
    {
        this.createTime = new Date();
        this.body = body;
        this.sendTo = sendTo;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getProcessingTime()
    {
        return processingTime;
    }

    public void setProcessingTime(Date processingTime)
    {
        this.processingTime = processingTime;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public Object getBody()
    {
        return body;
    }

    public void setBody(Object body)
    {
        this.body = body;
    }

    public String getSendTo()
    {
        return sendTo;
    }

    public void setSendTo(String sendTo)
    {
        this.sendTo = sendTo;
    }

    public String getSendNode()
    {
        return sendNode;
    }

    public void setSendNode(String sendNode)
    {
        this.sendNode = sendNode;
    }

    public String getProcessNode()
    {
        return processNode;
    }

    public void setProcessNode(String processNode)
    {
        this.processNode = processNode;
    }

}
