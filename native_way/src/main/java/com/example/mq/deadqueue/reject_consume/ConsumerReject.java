package com.example.mq.deadqueue.reject_consume;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者消费业务队列的消息，由于处理过程中发生异常，于是进行了nck或者reject操作,被nck或reject的消息由RabbitMQ投递到死信交换机中,
 * 死信交换机将消息投入相应的死信队列
 */
public class ConsumerReject {
    //普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag, message) ->{
            String msg = new String(message.getBody(), "UTF-8");
            if(msg.equals("info5")){
                System.out.println("Consumer01接受的消息是："+msg+"： 此消息是被C1拒绝的");
                //basic.reject方法拒绝deliveryTag对应的消息，deliveryTag可以理解为消息的id
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            }else {
                System.out.println("Consumer01接受的消息是："+msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }

        };
        //开启手动应答，也就是关闭自动应答
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,consumerTag -> {});
    }
}
