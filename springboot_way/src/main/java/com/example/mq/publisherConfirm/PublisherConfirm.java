package com.example.mq.publisherConfirm;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 配置发布者消息确认回调
 *
 * 消息确认机制之发布者确认：生产者/发布者发布的消息已经"成功"发送出去了.
 *
 * 生产者推送消息到消息队列会触发两个回调函数ConfirmCallback和RetrunCallback，从消息推送的结果来看，一共有4种情况：
 * 1消息推送到server，但是在server里找不到交换机， 这种情况触发的是ConfirmCallback 回调函数。
 * 2消息推送到server，找到交换机了，但是没找到队列 ，这种情况ConfirmCallback和RetrunCallback两个函数都被调用了
 * 3消息推送到sever，交换机和队列啥都没找到， 这种情况同情况1
 * 4消息推送成功，  这种情况只会调用ConfirmCallback
 *
 * application.yaml需要配置：
 *   rabbitmq:
 *     publisher-confirm-type: correlated  #确认消息已发送到交换机(Exchange)
 *     publisher-returns: true   #确认消息已发送到队列(Queue)
 * @return
 */
@RestController
public class PublisherConfirm {

    @Autowired//使用RabbitTemplate,这提供了接收/发送等等方法
    RabbitTemplate rabbitTemplate;


    /**
     * ① 消息推送到server，但是在server里找不到交换机
     * 也就是把消息发送到不存在的交换机上
     * @return
     */
    @GetMapping("/testMessageAck")
    public String TestMessageAck() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRouting", map);
        return "ok";
    }


    /**
     * ②消息推送到server，找到交换机了，但是没找到队列
     * lonelyDirectExchange交换机没有绑定到任何队列，把消息推送到名为lonelyDirectExchange的交换机上
     * 这种情况，ConfirmCallback和RetrunCallback两个函数都被调用了
     * 消息是推送成功到服务器了的，所以ConfirmCallback对消息确认情况是true；
     * 而在RetrunCallback回调函数的打印参数里面可以看到，消息是推送到了交换机成功了，但是在路由分发给队列的时候，找不到队列，所以报了错误 NO_ROUTE 。
     * @return
     */
    @GetMapping("/testMessageAck2")
    public String TestMessageAck2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: lonelyDirectExchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("lonelyDirectExchange", "TestDirectRouting", map);
        return "ok";
    }


    /**
     * ④消息推送成功:  只会调用ConfirmCallback
     * http://localhost:8082/testSendDirectMessage
     * RabbitBean.bindingGoodDirectExchange绑定规则，
     * 路由键等于good的消息会被goodDirectExchange交换机发送到goodQueque队列
     *
     *
     * @return
     */
    @GetMapping("/testSendDirectMessage")
    public String sendDirectMessage() {
        String messageData = "test message, hello!";
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("goodDirectExchange", "good", messageData);
        return "ok";
    }


}
