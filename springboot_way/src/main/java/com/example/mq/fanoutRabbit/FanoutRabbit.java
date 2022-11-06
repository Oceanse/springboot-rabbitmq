package com.example.mq.fanoutRabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *  创建三个队列 ：fanout.A   fanout.B  fanout.C
 *  将三个队列都绑定在交换机 fanoutExchange 上
 *  因为是扇型交换机, 路由键无需配置,配置也不起作用
 */
@Configuration
public class FanoutRabbit{


    /**
     * 队列 ：fanout.A, 设置别名，消除bean重名
     * @return
     */
    @Bean("AQueue")
    public Queue queueA() {
        return new Queue("fanout.A");
    }

    /**
     * 队列 ：fanout.B,  设置别名，消除bean重名
     * @return
     */
    @Bean("BQueue")
    public Queue queueB() {
        return new Queue("fanout.B");
    }

    /**
     * 队列 ：fanout.C, 设置别名，消除bean重名
     * @return
     */
    @Bean("CQueue")
    public Queue queueC() {
        return new Queue("fanout.C");
    }


    /**
     * 交换机 ：fanoutExchange
     * @return
     */
    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    /**
     * 扇形交换机(广播交换机)的绑定无需配置路由键
     * @return
     */
    @Bean
    Binding bindingExchangeA() {
        return BindingBuilder.bind(queueA()).to(fanoutExchange());
    }

    /**
     * 扇形交换机(广播交换机)的绑定无需配置路由键
     * @return
     */
    @Bean
    Binding bindingExchangeB() {
        return BindingBuilder.bind(queueB()).to(fanoutExchange());
    }

    /**
     * 扇形交换机(广播交换机)的绑定无需配置路由键
     * @return
     */
    @Bean
    Binding bindingExchangeC() {
        return BindingBuilder.bind(queueC()).to(fanoutExchange());
    }
}
