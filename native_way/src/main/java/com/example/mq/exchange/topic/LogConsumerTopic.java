package com.example.mq.exchange.topic;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 主题交换机：模糊匹配，类似于组播
 * 发送到类型是 topic 交换机的消息的 routing_key 不能随意写，必须满足一定的要求，它必须是一个单词列表，以点号分隔开。这些单词可以是任意单词
 * 比如说："stock.usd.nyse", "nyse.vmw", "quick.orange.rabbit" 这种类型的。
 * 当然这个单词列表最多不能超过 255 个字节。
 * 在这个规则列表中，其中有两个替换符是大家需要注意的：
 * *(星号)可以代替一个位置
 * #(井号)可以替代零个或多个位置
 * eg:
 * 中间带 orange 带 3 个单词的字符串 (*.orange.*)
 * 最后一个单词是 rabbit 的 3 个单词 (*.*.rabbit)
 * 第一个单词是 lazy 的多个单词 (lazy.#)
 *
 * 当一个队列绑定键是 #，那么这个队列将接收所有数据，就有点像 fanout 了
 * 如果队列绑定键当中没有 # 和 * 出现，那么该队列绑定类型就是 direct 了
 *
 */
public class LogConsumerTopic {
    //交换机的名称
    public static final String EXCHANGE_NAME = "topic_logs";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName = "Q1";
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"*.orange.*");
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列："+queueName+"  绑定键:"+message.getEnvelope().getRoutingKey());
        };
        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag ->{});
    }
}
