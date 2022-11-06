package com.example.mq.topicRabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbit {

    //绑定键
    public final static String man = "topic.man";
    public final static String woman = "topic.woman";


    /**
     * 声明队列
     */
    @Bean
    public Queue firstQueue() {
        return new Queue(TopicRabbit.man);
    }

    /**
     * 声明消息队列： topic.man
     */
    @Bean
    public Queue secondQueue() {
        //参数是队列的名字
        return new Queue(TopicRabbit.woman);
    }


    /**
     * 声明消息队列： topic.woman
     */
    @Bean
    TopicExchange exchange() {
        //参数是队列的名字
        return new TopicExchange("topicExchange");
    }


    /**
     * 将firstQueue和topicExchange绑定,而且绑定的键值为topicRoute.man
     * 这样只要是消息携带的路由键是topic.man,才会分发到该队列
     * @return
     */
    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(firstQueue()).to(exchange()).with("topicRoute.man");
    }


    /**
     * 将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topicRoute.#
     * 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
     * @return
     */
    @Bean
    Binding bindingExchangeMessage2() {
        return BindingBuilder.bind(secondQueue()).to(exchange()).with("topicRoute.#");
    }

}
