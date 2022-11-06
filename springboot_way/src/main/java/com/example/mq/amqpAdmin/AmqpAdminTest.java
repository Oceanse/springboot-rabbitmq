package com.example.mq.amqpAdmin;

import com.example.mq.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * AmqpAdmin可以用来创建删除管理 交换机、消息队列、绑定等
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AmqpAdminTest {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    public void createExchange(){
        //创建交换机
        amqpAdmin.declareExchange(new DirectExchange("amqpAdminCreatedExchange"));
        //创建队列
        amqpAdmin.declareQueue(new Queue("amqpAdminCreatedQueue"));
        //创建绑定规则
        amqpAdmin.declareBinding(new Binding("amqpAdminCreatedQueue", Binding.DestinationType.QUEUE,"amqpAdminCreatedExchange","amqp",null));
    }


}
