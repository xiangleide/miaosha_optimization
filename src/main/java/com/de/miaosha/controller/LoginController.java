package com.de.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.result.Result;
import com.de.miaosha.service.MiaoshaUserService;
import com.de.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	MiaoshaUserService userService;
	
	private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping("/to_login")
	public String tologin() {
		return "login";
	}
	
	@RequestMapping("/do_login")
	@ResponseBody
	public Result<String> dologin(HttpServletResponse response,@Valid LoginVo loginVo) {
		LOGGER.info(loginVo.toString());
		
		String token =userService.login(response,loginVo);
	    return Result.success(token);
		
	}
}
