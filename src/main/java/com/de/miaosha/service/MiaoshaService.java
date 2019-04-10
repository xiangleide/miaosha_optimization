package com.de.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.de.miaosha.domain.MiaoshaOrder;
import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.domain.OrderInfo;
import com.de.miaosha.redis.MiaoshaKey;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;
	
	@Autowired
	RedisService redisService;
	
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存  下订单  写入秒杀订单 三个步骤为一个事务
		boolean result = goodsService.reduceStock(goods);
		if (result == false) {
			setGoodsOver(goods.getId());
			return null;
		}
		//order_info miaosha_order
		return orderService.createOrder(user,goods);
		
	}

	public long getMiaoshaResult(Long userId, Long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (order != null) {
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	private boolean getGoodsOver(Long goodsId) {
		return redisService.exist(MiaoshaKey.isGoodsOver, ""+goodsId);
	}

}
