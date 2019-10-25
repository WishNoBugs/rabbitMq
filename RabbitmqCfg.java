package com.chinawanbang.hmr.common.cfg;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitmqCfg {

    private static final String DEQUE_QUEUE_NAME = "dequeQueue"; //用于延迟消费的队列
    private static final String ENQUE_QUEUE_NAME = "enqueQueue";
    private static final String DEQUE_QUEUE_NAME_KEY = "dequeQueueKey";
    private static final String DELAY_EXCHANGE="exchange_delay";

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(DELAY_EXCHANGE);
    }
    @Bean
    public Queue dequeQueue(){
        return new Queue(DEQUE_QUEUE_NAME,true,false,false);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(dequeQueue()).to(directExchange()).with(DEQUE_QUEUE_NAME_KEY);
    }
    //配置死信队列，即入队队列
    @Bean
    public Queue deadLetterQueue() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 20000);
        args.put("x-dead-letter-exchange", DELAY_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEQUE_QUEUE_NAME_KEY);
        return new Queue(ENQUE_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        //若使用confirm-callback或return-callback，必须要配置publisherConfirms或publisherReturns为true
        //每个rabbitTemplate只能有一个confirm-callback和return-callback，如果这里配置了，那么写生产者的时候不能再写confirm-callback和return-callback
        //使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        /**
         * 如果消息没有到exchange,则confirm回调,ack=false
         * 如果消息到达exchange,则confirm回调,ack=true
         * exchange到queue成功,则不回调return
         * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }

}
