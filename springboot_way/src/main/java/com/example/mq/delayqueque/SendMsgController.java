package com.example.mq.delayqueque;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * 发起一个请求：http://localhost:8888/ttl/sendMsg/嘻嘻嘻, 同一个消息由于路由键不同，最后到达两个不同TTL的队列.
 * 两个队列中的消息会分别在10s和30s后变成死信，然后转移到死信队列，再设置一个消费该死信队列的消费者处理这些死信，这样就实现了延时队列
 *
 * 如果没有设置消费者：通过MQ前端页面能看到，死信队列10s后收到一条消息，30s后又收到一条消息
 *
 * 如果设置了消费者：第一条消息在 10S 后变成了死信消息，进入死信队列，然后被消费者消费掉，第二条消息在 40S 之后变成了死信
 * 消息，进入死信队列， 然后被消费掉，这样一个延时队列就打造完成了。
 *
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //开始发消息: ：http://localhost:8082/ttl/sendMsg/嘻嘻嘻
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message) {
        log.info("当前时间:{},发送一条信息给两个TTL队列：{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl为10s的队列:" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl为30s的队列:" + message);
    }
}