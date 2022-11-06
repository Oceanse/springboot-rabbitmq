package com.example.mq.ack.consumer_ack.demo1.autoack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    //对列名称
    public static final String QUEUE_NAME="helloQueue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //broker ip
        factory.setHost("localhost");
        //broker端口
        factory.setPort(5673);
        //用户名
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();




        /*
         * 生产一个对列
         * 1.对列名称
         * 2.对列里面的消息是否持久化，默认情况下，消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费 false：只能一个消费者消费
         * 4.是否自动删除，最后一个消费者端开链接以后，该队列是否自动删除，true表示自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //发消息
        String message = "Hello,world2";

        /**
         * 发送一个消息
         * 1.发送到哪个交换机
         * 2.路由的key值是哪个本次是队列的名称
         * 3.其他参数信息
         * 4.发送消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("消息发送完毕");



    }
}
