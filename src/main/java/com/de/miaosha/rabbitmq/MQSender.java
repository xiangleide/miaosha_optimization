package com.de.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.de.miaosha.redis.RedisService;

@Service
public class MQSender {

	private static Logger log = LoggerFactory.getLogger(MQSender.class);
	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	public void sendMiaoshaMessage(MiaoshaMessage message) {
		String msg = RedisService.beanToString(message);
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
		log.info("send message:{}",msg);
	}
/*	
	//Direct模式
	public void send(Object message) {
		String msg = RedisService.beanToString(message);
		amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
		log.info("send message:{}",msg);
	}
	
	//Topic模式 
	public void sendTopic(Object message) {
		String msg = RedisService.beanToString(message);
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
		
		log.info("send topic message:{}",msg);
	}
	
	//Fanout模式(广播)
	public void sendFanout(Object message) {
		String msg = RedisService.beanToString(message);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
		
		log.info("send fanout message:{}",msg);
	}
	
	//Header模式
	public void sendHeader(Object message) {
		String msg = RedisService.beanToString(message);
		MessageProperties properties = new MessageProperties();
		properties.setHeader("header1", "value1");
		properties.setHeader("header2", "value2");
		Message obj = new Message(msg.getBytes(), properties);
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
		
		log.info("send fanout message:{}",msg);
	}
*/
}
