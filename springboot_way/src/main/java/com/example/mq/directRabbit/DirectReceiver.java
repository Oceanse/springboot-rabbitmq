package com.example.mq.directRabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 消息接收监听类,只会监听TestDirectQueue消息队列，只要消息队列生成新的消息，就会回调相应的handler方法
 * 发送消息用的Map/List，接收消息就用Map/List，统一消息类型就可以正常收发;
 * 配置多个消费者(监听器)消费一个消息队列， 会使用轮询的方式对消息进行消费，而且不存在重复消费
 */
@Component
@RabbitListener(queues = "TestDirectQueue")//监听的队列名称 TestDirectQueue
public class DirectReceiver {

      /**
     * 只要消息队列有消息，该方法就能监听到，并回调该方法；
     * 如果消息队列没有消息，可以向消息队列发送一条消息，就能触发回调当前方法
     * 比如：SendMessageController.sendDirectMessage()
     * @param testMessage
     */
    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("DirectReceiver消费者收到消息  : " + testMessage.toString());
    }


    /**
     * 只要消息队列有消息，该方法就能监听到，并回调该方法；
     * 如果消息队列没有消息，可以向消息队列发送一条消息，就能触发回调当前方法
     * 比如：SendMessageController.sendDirectMessage()
     * @param message
     */
    @RabbitHandler
    public void process2(List message) {
        System.out.println("DirectReceiver2消费者收到消息  : " + message.toString());
    }



}
