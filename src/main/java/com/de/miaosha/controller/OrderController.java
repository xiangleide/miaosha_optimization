package com.de.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.domain.OrderInfo;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.result.CodeMsg;
import com.de.miaosha.result.Result;
import com.de.miaosha.service.GoodsService;
import com.de.miaosha.service.MiaoshaUserService;
import com.de.miaosha.service.OrderService;
import com.de.miaosha.vo.GoodsVo;
import com.de.miaosha.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model,MiaoshaUser user,
			@RequestParam("orderId")long orderId) {

		if (user ==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo order  = orderService.getOrderById(orderId);
		if (order == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long goodsId = order.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo orderDetailVo= new OrderDetailVo();
		
		orderDetailVo.setGoods(goods);
		orderDetailVo.setOrder(order);
		
		return Result.success(orderDetailVo);
	}
}
