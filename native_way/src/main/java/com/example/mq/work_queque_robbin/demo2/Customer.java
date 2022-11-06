package com.example.mq.work_queque_robbin.demo2;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * c1和c2处理消息速度不同，但是依然会轮训消费队列中的消息
 * 生产者发布1，2，3，4，5，6，7，8
 * 正常是c1消费1，3，5，7，   c2消费2，4，6，8
 */
public class Customer {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
    Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

    DeliverCallback deliverCallback =(consumerTag, message) ->{

        //沉睡1S
        SleepUtils.sleep(1);
        System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));

    };

    CancelCallback cancelCallback = (consumerTag -> {
        System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

    });
        channel.basicConsume(TASK_QUEUE_NAME,true,deliverCallback,cancelCallback);
}
}
