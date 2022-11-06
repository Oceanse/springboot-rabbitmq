package com.example.mq.rabbit_template;

import com.example.mq.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * RabbitTemplate收发消息
 */
@SpringBootTest(classes = BootApplication.class)
@RunWith(SpringRunner.class)
public class RabbitTemplateDemo {

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 消息发送到消息队列
     */
    @Test
    public void testSendWithRabbitTemplate(){
        rabbitTemplate.convertAndSend("directExchange", "testMessage", "This is a test message");
    }


    /**
     * 从消息队列接收数据
     */
    @Test
    public void testReceiveWithRabbitTemplate(){
        Object msg = rabbitTemplate.receiveAndConvert("directQueue");
        System.out.println("msg = " + msg);
    }
}
