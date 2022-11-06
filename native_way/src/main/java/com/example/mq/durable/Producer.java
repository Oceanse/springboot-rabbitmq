package com.example.mq.durable;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 当 RabbitMQ 服务停掉以后，消息生产者发送过来的消息不丢失要如何保障？
 * 默认情况下 RabbitMQ 退出或由于某种原因崩溃时，它忽视队列和消息，除非告知它不要这样做。
 * 确保消息不会丢失需要做两件事：我们需要将队列和消息都标记为持久化。
 *
 * 1 队列持久化
 * 之前我们创建的队列都是非持久化的，RabbitMQ 如果重启的化，该队列就会被删除掉，如果要队列实现持久化需要在声明队列
 * 的时候把 durable 参数设置为true，代表开启持久化
 *
 * 2消息持久化
 * 需要在消息生产者发布消息的时候，开启消息的持久化;
 * 在 basicPublish 方法的第三个参数添加这个属性： MessageProperties.PERSISTENT_TEXT_PLAIN
 *
 * 将消息标记为持久化并不能完全保证不会丢失消息。尽管它告诉 RabbitMQ 将消息保存到磁盘，但是这里依然存在
 * 当消息刚准备存储在磁盘的时候 但是还没有存储完，消息还在缓存的一个间隔点。此时并没 有真正写入磁盘。持久性保证并不强，
 * 但是对于我们的简单任务队列而言，这已经绰绰有余了。
 *
 * #
 */
public class Producer {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //开启持久化
        boolean durable = true;

        //声明队列
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
        //在控制台中输入信息
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息：");
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+ message);
        }

    }
}
