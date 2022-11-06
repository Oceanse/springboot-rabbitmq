package com.example.mq.work_queque_robbin.demo1;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 案例中生产者叫做 Task，测试发现work01和work02会轮流消费消息，也就是轮询消费
 */
public class Task01 {
    //队列名称
    public static final String QUEUE_NAME="helloQueue";

    //发送大量消息
    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //发送消息
        //从控制台当中接受信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("消息发送完成:"+message);
        }
    }
}
