package com.example.mq.deadqueue.ttl;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 配置死信队列的消费者
 * 配置死信队列的消费者来对死信队列中的消息进行相应的处理
 * 比如从死信队列拉取消息，然后发送邮件、短信、钉钉通知来通知开发人员关注
 *
 * 如果同时启动生产者消费者，那么10s后，死信队列的消费者将会消费到消息，死信队列中的消息也会相应被移除
 */
public class ConsumerDeadQueue {
    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        System.out.println("等待接收死信消息...");

        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("Consumer02接受的消息是："+new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag -> {});
    }
}
