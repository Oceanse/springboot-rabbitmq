package com.example.mq.publisherConfirm;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "goodQueque")//监听的队列名称 goodQueque
public class Receiver {
    /**
     * 只要消息队列有消息，该方法就能监听到，并回调该方法；
     * 如果消息队列没有消息，可以向消息队列发送一条消息，就能触发回调当前方法
     * @param testMessage
     */
    @RabbitHandler
    public void process(String testMessage) {
        System.out.println("Receiver消费者收到消息  : " + testMessage.toString());
    }

}
