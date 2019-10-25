package com.chinawanbang.hmr.common.cfg;

import com.chinawanbang.hmr.system.controller.UserController;
import com.rabbitmq.client.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;


@Component
public class DirectReceiver {
    private static final Log log = LogFactory.getLog(UserController.class);
//    @RabbitListener(queues = "${mail.queue.name}",containerFactory = "singleListenerContainer")
//    public void consumeUserLogQueue(Message message, Channel channel) throws Exception {
//        try{
//            log.info("receive: "+new String(message.getBody(),"UTF-8"));
//            String[] arr = {null,null,null,"b"};
//            String testString = arr[new Random().nextInt(arr.length)];
//            testString.toString();
//            // 消息确认
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
//        }catch(Exception e){
//            log.info(e.getMessage());
//            // 处理消息失败，将消息重新放回队列
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
//        }
//    }
    @RabbitListener(queues = "dequeQueue")
    public void process(String msg) {
        System.out.println("接收消息:"+ LocalDateTime.now().toString()+" 内容："+msg);
    }


}
