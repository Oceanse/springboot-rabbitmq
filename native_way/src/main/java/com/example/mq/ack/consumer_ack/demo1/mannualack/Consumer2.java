package com.example.mq.ack.consumer_ack.demo1.mannualack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者完成一个任务可能需要一段时间，如果其中一个消费者处理一个长的任务并仅只完成了部分突然它挂掉了，
 * 会发生什么情况。RabbitMQ 一旦向消费者传递了一条消息，便立即将该消息标记为删除。在这种情况下，
 * 突然有个消费者挂掉了，我们将丢失正在处理的消息。
 * <p>
 * 为了保证消息在发送过程中不丢失，引入消息应答机制，消息应答就是：消费者在接收到消息并且处理该消息之后，
 * 告诉 rabbitmq 它已经处理了，rabbitmq 可以把该消息删除了。
 * <p>
 * 自动应答: 消息发送后立即被认为已经传送成功，默认消息采用的是自动应答，所以我们要想实现消息消费过程中不丢失，需要把自动应答改为手动应答
 * 手动应答:手动显式确认，消费者确认消息消费成功后，向队列应答确认
 * #
 */
public class Consumer2 {
    //队列的名称
    public static final String QUEUE_NAME = "helloQueue";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //broker ip
        factory.setHost("localhost");
        //broker端口
        factory.setPort(5673);
        //用户名
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        /**
         *  c1消费者消费过程中发生异常，因为采取手动应答，这时候消费者不会向MQ应答ack，消息队列中的消息也就不会被删除了，
         *  消费者c2可以从队列消费数据
         */
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("c2收到消息：" + new String(message.getBody()));
            //手动确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

        };
        //取消消息时的回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息消费被中断");
        };

        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答true：代表自动应答false:代表手动应答
         * 3.消费者未成功消费的回调
         * 4.消费者取消消费的回调
         */
        //采用手动应答
        boolean autoAck = false;
        System.out.println("c2等待接收消息");
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);

    }
}
