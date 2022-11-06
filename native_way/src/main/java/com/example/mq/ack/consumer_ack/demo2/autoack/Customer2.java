package com.example.mq.ack.consumer_ack.demo2.autoack;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者启用两个线程，消费 1 一秒消费一个消息，消费者 2 十秒消费一个消息，然后在消费者 2 消费消息的时候，停止运行;
 * 生产者发布1，2，3，4，5，6，7，8
 * 正常是c1消费1，3，5，7，   c2消费2，4，6，8
 * 测试发现，c1消费了1，3，5，7， c2消费完第一个消息2挂掉后，c1还是只消费了1，3，5，7， 也就是消费失败的数据4，6，8没有被c1消费，也就是数据丢失了
 */
public class Customer2 {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
    Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C2等待接受消息处理时间较长");

    DeliverCallback deliverCallback =(consumerTag, message) ->{

        //沉睡10S，
        SleepUtils.sleep(10);
        System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));

    };

    CancelCallback cancelCallback = (consumerTag -> {
        System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

    }); //自动应答
        channel.basicConsume(TASK_QUEUE_NAME,true,deliverCallback,cancelCallback);
}
}
