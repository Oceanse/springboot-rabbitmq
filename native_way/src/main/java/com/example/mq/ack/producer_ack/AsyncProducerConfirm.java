package com.example.mq.ack.producer_ack;

import com.example.mq.work_queque_robbin.demo1.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;


/**
 * 生产者不用等待broker的确认就可以继续发消息；
 * broker收到消息后，会选择一个时机通知生产者，通知的具体方式就是调用生产者的回调方法
 */
public class AsyncProducerConfirm {
    public static final int MESSAGE_COUNT = 1000; //Ctrl+Shift+U 变大写

    public static void main(String[] args) throws Exception {
        publishMessageAsync(); //发布1000个异步发布确认消息，耗时:43ms
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception{

        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, true, false, null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //消息确认回调的函数
        ConfirmCallback ackCallback = (deliveryTag,multiple) ->{
            System.out.println("确认的消息:"+deliveryTag);
        };
        /**
         * 1.消息的标记
         * 2.是否为批量确认
         */
        //消息确认失败回调函数
        ConfirmCallback nackCallback= (deliveryTag,multiple) ->{
            System.out.println("未确认的消息:"+deliveryTag);
        };

        //准备消息的监听器 监听那些消息成功了，哪些消息失败了
        /**
         * 异步通知，也就是生产者不用等待确认，可以尽情发消息，broker无论无论收没收到消息，都会选择一个时机通知生产者
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallback,nackCallback);

        //批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"消息";
            channel.basicPublish("",queueName,null,message.getBytes());
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个异步发布确认消息，耗时:"+(end-begin)+"ms");
    }
}
