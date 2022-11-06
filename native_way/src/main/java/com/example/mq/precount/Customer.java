package com.example.mq.precount;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 官方的Prefetching Messages介绍
 * For cases when multiple consumers share a queue, it is useful to be able to specify how many messages
 * each consumer can be sent at once before sending the next acknowledgement. This can be used as a simple
 * load balancing technique or to improve throughput if messages tend to be published in batches.
 *
 * MQ往通道道上发送消息默认是异步发送的，另外来自消费者的手动确认本质上也是异步的
 * 因此这里就存在一个未确认的消息缓冲区，因此希望开发人员能限制此缓冲区的大小，以避免缓冲区里面无限制的未确认消息问题。
 * 个人理解：
 * 1 通道上的消息都是等待被消费者消费的，所以都还没得到确认应答
 * 2 预取值就是通道上能堆积的最大消息数量，也就是通道上允许的未确认消息的最大数量。
 * 3 预取值简单理解为消费者端的"通道缓冲区"，MQ可以批量向该缓冲区发送消息，比如可以一次发送10条，这样就比MQ每次发送一条要快的多，因此能提高吞吐量
 * 4 当缓冲区满了后，也就是通道上消息的数量达到了预取值，那么MQ就会停止向通道发送消息，直到通道上的至少一个消息被消费确认(个人理解确认后消息会从缓冲区中被删除)；
 *   例如，假设在通道上有未确认的消息 5、6、7，8，并且通道的预取计数设置为 4，此时 RabbitMQ 将不会在该通道上再传递任何消息，除非至少有一个未应答的消息被 ack。
 *   比方说 tag=6 这个消息刚刚被确认 ACK，RabbitMQ 将会感知这个情况到并再发送一条消息。消息应答和 QoS 预取值对用户吞吐量有重大影响。
 * 5 预取值为 1 是最保守的。也就失去了批量发送的意义，MQ每次只能往通道发送一个消息，然后等消息被消费确认后再发送下一个消息，这将使吞吐量变得很低，
 *   特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境中，对于大多数应用来说，稍微高一点的值将是最佳的。
 * 6 消费者如果采取了自动应答，又实用了无限预处理，那么MQ将会高速向消费者发送消息，在这种情况下消费端已传递但尚未处理的消息的数量也会增加，从而增加了消费者的 RAM 消耗
 */
public class Customer {
    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        DeliverCallback deliverCallback =(consumerTag, message) ->{

            //沉睡1S
            SleepUtils.sleep(1);
            System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));
            //手动应答
            /**
             * 1.消息的标记Tag
             * 2.是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };

        CancelCallback cancelCallback = (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

        });

        //prefetchCount = 1,代表不公平分发
        //prefetchCount > 1，则代表预取值,预取值为7; 预期值简单理解为"通道缓冲区"，通道上能堆积的最大消息数量
        int prefetchCount = 7;
        channel.basicQos(prefetchCount);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
