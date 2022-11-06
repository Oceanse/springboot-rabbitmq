package com.example.mq.deadqueue.ttl;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * https://blog.csdn.net/dingd1234/article/details/125024880
 * https://blog.csdn.net/weixin_44688301/article/details/116237294
 *
 *
 * 死信的概念:
 * 死信，顾名思义就是无法被消费的消息(官网中对应的单词为“Dead Letter”)。一般来说，Producer 将消息投递到 Broker 或者直接到 Queue 里了，Consumer 从 Queue
 * 取出消息进行消费，但某些时候由于特定的原因导致 Queue 中的某些消息无法被消费，这样的消息如果没有后续的处理，就变成了死信，
 *
 * 死信的来源:
 * “死信”是RabbitMQ中的一种消息机制，当你在消费消息时，如果队列里的消息出现以下情况之一，那么该消息将成为“死信”。
 * 1 消息 TTL 过期
 * 2 队列达到最大长度（队列满了，无法再添加数据到 mq 中）
 * 3 消息被拒绝（basic.reject 或 basic.nack）并且 requeue=false（不再重新入队）
 *
 * 死信的处理：
 * “死信”消息会被RabbitMQ进行特殊处理，如果配置了死信队列信息，那么该消息将会被丢进死信队列中，如果没有配置，则该消息将会被丢弃。
 *
 * 配置死信队列：
 * 配置业务队列，绑定到业务交换机上
 * 为业务队列配置死信交换机和路由key
 * 为死信交换机配置死信队列
 * 注意，并不是直接声明一个公共的死信队列，然后所以死信消息就自己跑到死信队列里去了。而是为每个需要使用死信的业务队列配置一个死信交换机和死信路由key。
 *
 * 使用场景：
 * 通过上面的信息，我们已经知道如何使用死信队列了，那么死信队列一般在什么场景下使用呢？
 * 一般用在较为重要的业务队列中，确保未被正确消费的消息不被丢弃，一般发生消费异常可能原因主要有由于消息信息本身存在错误导致处理异常，
 * 处理过程中参数校验异常，或者因网络波动导致的查询异常等等，当发生异常时，当然不能每次通过日志来获取原消息，然后让运维帮忙重新投递
 * 消息（没错，以前就是这么干的= =）。通过配置死信队列，可以让未正确处理的消息暂存到另一个队列中，待后续排查清楚问题后，编写相应的处
 * 理代码来处理死信消息，这样比手工恢复数据要好太多了。
 *
 * 现象：生产者发布10条消息后，通过RabbitMQ web界面观察normal_queue，会发现10条数据，10s后，所有的消息都达到了TTL,所以消息全部从normal_queue
 * 转移到了dead_queue，dead_queue中会存在10条数据
 * 注意创建同名队列，如果队里参数改变了，所以需要把原先队列删除
 */
public class ProducerTTL {
    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机的名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明死信和普通交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明普通队列
        Map<String,Object> arguments = new HashMap<>();
        //过期时间 10s,过期时间可以在队列中统一设置，或者由生产者发布消息时候指定更加灵活
        //arguments.put("x-message-ttl",10000);
        //设置正常队列中的死信对应的交换机以及路由键，这样死信就能通过制定的交换机和路由键转移到死信队列
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","dead");
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);

        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        //绑定普通的交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"normal");

        //绑定死信的交换机与死信的队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"dead");

        //发布消息
        //死信消息 设置ttl时间 live to time 单位是ms
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i <11 ; i++) {
            String message = "info"+i;
            channel.basicPublish(NORMAL_EXCHANGE,"normal",properties,message.getBytes());
           // channel.basicPublish(DEAD_EXCHANGE,"dead",properties,message.getBytes());
        }
    }
}
