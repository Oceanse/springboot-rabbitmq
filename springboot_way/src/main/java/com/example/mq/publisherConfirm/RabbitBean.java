package com.example.mq.publisherConfirm;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitBean {

    /**
     * 创建名字是lonelyDirectExchange的交换机，该交换机没有绑定到任何消息队列
     * @return
     */
    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }

    /**
     * 创建名字是goodDirectExchange的交换机
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("goodDirectExchange");
    }

    /**
     * 创建队列 ：goodQueque
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue("goodQueque");
    }



    /**
     * 扇形交换机(广播交换机)的绑定无需配置路由键
     * @return
     */
    @Bean
    Binding bindingGoodDirectExchange() {
        return BindingBuilder.bind(queue()).to(directExchange()).with("good");
    }
}
