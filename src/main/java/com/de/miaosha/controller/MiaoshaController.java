package com.de.miaosha.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.domain.MiaoshaOrder;
import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.domain.OrderInfo;
import com.de.miaosha.rabbitmq.MQSender;
import com.de.miaosha.rabbitmq.MiaoshaMessage;
import com.de.miaosha.redis.GoodsKey;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.result.CodeMsg;
import com.de.miaosha.result.Result;
import com.de.miaosha.service.GoodsService;
import com.de.miaosha.service.MiaoshaService;
import com.de.miaosha.service.MiaoshaUserService;
import com.de.miaosha.service.OrderService;
import com.de.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	@Autowired
	MQSender sender;
	
	private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

	/**
	 * 系统初始化
	 */
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		List<GoodsVo> goodsList= goodsService.listGoodsVo();
		if (goodsList == null) {
			return;
		}
		for (GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
	/**
	 * QPS:1341
	 * 5000*10
	 * 
	 * 优化后QPS:2310
	 */
	
	/**
	 * GET POST有何区别？
	 * GET幂等
	 */
	@RequestMapping(value ="/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(Model model,MiaoshaUser user,
			@RequestParam("goodsId") Long goodsId) {
		model.addAttribute("user", user);
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//内存标记，减少redis访问
		boolean over = localOverMap.get(goodsId);
		if (over) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//预减库存
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);
		if (stock<0) {
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
		if (order != null) {
			return Result.error(CodeMsg.REPEAT_MIAOSHA);
		}
		
		//入队
		MiaoshaMessage message = new MiaoshaMessage();
		message.setUser(user);
		message.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(message);
		
		return Result.success(0);//排队中
		/*
		//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if (stock <=0 ) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
		if (order != null) {
			return Result.error(CodeMsg.REPEAT_MIAOSHA);
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
		return Result.success(orderInfo);
		*/
	}
	
	/**
	 * return orderId:成功
	 * return -1 :   秒杀失败
	 * return 0: 排队中
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value ="/result",method = RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
			@RequestParam("goodsId") Long goodsId) {
		model.addAttribute("user", user);
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
		return Result.success(result);
	}

}
