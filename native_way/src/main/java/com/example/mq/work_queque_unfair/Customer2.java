package com.example.mq.work_queque_unfair;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 能者多劳
 * 在最开始的时候我们学习到 RabbitMQ 分发消息采用的轮询分发，但是在某种场景下这种策略并不是很好，
 * 比方说有两个消费者在处理任务，其中有个消费者 1 处理任务的速度非常快，而另外一个消费者 2 处理速度却很慢，
 * 这个时候我们还是采用轮询分发的化就会到这处理速度快的这个消费者很大一部分时间处于空闲状态，而处理慢的那个
 * 消费者一直在干活，这种分配方式在这种情况下其实就不太好，但是 RabbitMQ 并不知道这种情况它依然很公平的进行分发。
 * 为了避免这种情况，在消费者中消费消息之前，设置参数 channel.basicQos(1);
 *
 * 测试结果：
 * 生产者发布1，2，3，4，5，6，7，8， 消费者1消费速度慢，只消费了1； 消费者2消费速度快，消费了2，3，4，5，6，7，8；
 */
public class Customer2 {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C2等待接受消息处理时间较短");

        DeliverCallback deliverCallback = (consumerTag, message) -> {

            //沉睡1S
            SleepUtils.sleep(1);
            System.out.println("接受到的消息:" + new String(message.getBody(), "UTF-8"));

            //手动应答
            /**
             * 1.消息的标记Tag
             * 2.是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);


        };

        CancelCallback cancelCallback = (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

        });

        //设置不公平分发
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
