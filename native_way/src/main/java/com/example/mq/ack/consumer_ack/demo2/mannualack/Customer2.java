package com.example.mq.ack.consumer_ack.demo2.mannualack;

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
 * 采取手动确认后，测试发现：
 * c1消费了1，3，5，7， c2消费完第一个消息2挂掉后，c1继续消费4，6，8
 *
 * 个人猜测原因：4，6，8没有回复手动确认，所以还存在于队列中；然后MQ监听到c2已经挂掉，所以分发给c1进行消费
 * 官方解释：消息自动重新入队。如果消费者由于某些原因失去连接(其通道已关闭，连接已关闭或 TCP 连接丢失)，导致消息未发送 ACK 确认，RabbitMQ 将了解到消息未完全处理，并将对其重新排队。
 * 如果此时其他消费者可以处理，它将很快将其重新分发给另一个消费者。这样，即使某个消费者偶尔死亡，也可以确保不会丢失任何消息。
 */
public class Customer2 {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
    Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C2等待接受消息处理时间较长");

    DeliverCallback deliverCallback =(consumerTag, message) ->{

        //沉睡1S
        SleepUtils.sleep(10);
        System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));
        //手动应答
        /**
         * 1.消息的标记Tag
         * 2.是否批量应答 false表示不批量应答信道中的消息
         */
        channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

    };

    CancelCallback cancelCallback = (consumerTag -> {
        System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

    });
    //采用手动应答
    boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
}
}
