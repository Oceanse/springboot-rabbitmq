package com.example.mq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    /**
     * RabbitMQ 消息的序列化和反序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        // 生成的bean会被自动设置到RabbitTemplate，RabbitTemplate封装了收发消息的api
        // RabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter()
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 1 配置回调
     * 消息确认机制之发布者确认：生产者/发布者发布的消息已经"成功"发送出去了.
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
     *
     *
     * 2 配置消息序列化和反序列化
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        //RabbitMQ 消息的序列化和反序列化
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
            System.out.println("ConfirmCallback:     "+"确认情况："+ack);
            System.out.println("ConfirmCallback:     "+"原因："+cause);
        });

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("ReturnCallback:     "+"消息："+message);
            System.out.println("ReturnCallback:     "+"回应码："+replyCode);
            System.out.println("ReturnCallback:     "+"回应信息："+replyText);
            System.out.println("ReturnCallback:     "+"交换机："+exchange);
            System.out.println("ReturnCallback:     "+"路由键："+routingKey);
        });
        return rabbitTemplate;
    }

}
