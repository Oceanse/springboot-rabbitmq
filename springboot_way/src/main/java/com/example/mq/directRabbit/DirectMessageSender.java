package com.example.mq.directRabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class DirectMessageSender {
    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * http://localhost:8082/sendDirectMessage
     * DirectRabbit.bindingDirect绑定规则，
     * 路由键等于TestDirectRouting的消息会被TestDirectExchange交换机发送到TestDirectQueue队列
     * 可以去rabbitMq管理页面看看，是否推送成功：
     *
     * @return
     */
    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        for (int i = 0; i < 5; i++) {
            rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        }

        List<String> list = Arrays.asList("Apple", "grape", "orange");
        for (int i = 0; i < 5; i++) {
            rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", list);
        }
        return "ok";
    }
}
