package com.example.mq.delayqueque;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 死信队列中出现消息，该消费者就会进行消费
 */
@Component
public class DeadLetterConsumer {
    //死信队列的名称
    public static final String DEAD_LETTER_QUEUE="QD";
    @RabbitListener(queues = DEAD_LETTER_QUEUE)
    public void receiveA(Message message, Channel channel) throws IOException {
        System.out.println("收到死信消息：" + new String(message.getBody()));
    }
}
