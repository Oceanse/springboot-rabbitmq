package com.example.mq.topicRabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class TopicMessageSender {

    @Autowired//使用RabbitTemplate,这提供了接收/发送等等方法
    RabbitTemplate rabbitTemplate;

    /**
     * http://localhost:8082/sendTopicMessage1
     * 根据TopicRabbit.bindingExchangeMessage/bindingExchangeMessage2规则，
     * 路由键=topic.man, topicExchange交换机会把消息发送到topic.man和topic.woman两个队列
     * @return
     */
    @GetMapping("/sendTopicMessage1")
    public String sendTopicMessage1() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: M A N ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> manMap = new HashMap<>();
        manMap.put("messageId", messageId);
        manMap.put("messageData", messageData);
        manMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("topicExchange", "topicRoute.man", manMap);
        return "ok";
    }


    /**
     * http://localhost:8082/sendTopicMessage2
     * 根据TopicRabbit.bindingExchangeMessage/bindingExchangeMessage2规则，
     * 路由键=topic.woman, topicExchange交换机会把消息发送到topic.woman队列
     * @return
     */
    @GetMapping("/sendTopicMessage2")
    public String sendTopicMessage2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: woman is all ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> womanMap = new HashMap<>();
        womanMap.put("messageId", messageId);
        womanMap.put("messageData", messageData);
        womanMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("topicExchange", "topicRoute.woman", womanMap);
        return "ok";
    }


}
