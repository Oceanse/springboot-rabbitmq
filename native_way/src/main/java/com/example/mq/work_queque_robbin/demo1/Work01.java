package com.example.mq.work_queque_robbin.demo1;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者1：
 * Work Queues 是工作队列（又称任务队列）,一个消费者就是一个工作队列
 *
 * 轮询消费：多个消费者现成轮流消费消息，即每个工作队列都会依次获取一个消息进行消费
 */
public class Work01 {
    //队列的名称
    public static final String QUEUE_NAME = "helloQueue";


    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //消息的接受
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("work01接收到的消息:" + new String(message.getBody()));
        };

        //消息接受被取消时，执行下面的内容
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(consumerTag + "消息被消费者取消消费接口回调逻辑");
        };

        //消息的接收
        System.out.println("c1等待接收消息");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
