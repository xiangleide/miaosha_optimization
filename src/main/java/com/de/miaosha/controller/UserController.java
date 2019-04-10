package com.de.miaosha.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.result.Result;
import com.de.miaosha.service.MiaoshaUserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@RequestMapping("/info")
	@ResponseBody
	public Result<MiaoshaUser> toList(Model model,MiaoshaUser user) {

		return Result.success(user);
	}
}
