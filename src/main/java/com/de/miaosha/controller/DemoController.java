package com.de.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.domain.User;
import com.de.miaosha.rabbitmq.MQSender;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.redis.UserKey;
import com.de.miaosha.result.CodeMsg;
import com.de.miaosha.result.Result;
import com.de.miaosha.service.UserService;

@Controller
public class DemoController {

	@Autowired
	UserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	MQSender sender;
	
//	@RequestMapping("/mq")
//	@ResponseBody
//	public Result<String> topic() {
//		sender.send("hello,Elend");
//		return Result.success("Hello");
//	}
//	
//	@RequestMapping("/mq/topic")
//	@ResponseBody
//	public Result<String> mq() {
//		sender.sendTopic("hello,Elend");
//		return Result.success("Hello");
//	}
//	
//	@RequestMapping("/mq/fanout")
//	@ResponseBody
//	public Result<String> famout() {
//		sender.sendFanout("hello,Elend");
//		return Result.success("Hello");
//	}
//	
//	@RequestMapping("/mq/header")
//	@ResponseBody
//	public Result<String> header() {
//		sender.sendHeader("hello,Elend");
//		return Result.success("Hello");
//	}
	
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello world!";
	}
	
	//1.rest api json输出
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello.");
	}
	
	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
	}
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "leide");
		return "hello";
	}
	
	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet() {
		User user = userService.getById(1);
		return Result.success(user);
	}
	
	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbTx() {
		userService.tx();
		return Result.success(true);
	}
	
	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet() {
		User user = redisService.get(UserKey.getById,""+1,User.class);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> Set() {
		User user = new User();
		user.setId(1);
		user.setName("zhangsan");
		
		Boolean b = redisService.set(UserKey.getById,""+1, user);//UserKey:id1
		return Result.success(b);
	}
}
