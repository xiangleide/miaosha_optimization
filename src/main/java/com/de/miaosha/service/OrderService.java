package com.de.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.de.miaosha.dao.OrderDao;
import com.de.miaosha.domain.MiaoshaOrder;
import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.domain.OrderInfo;
import com.de.miaosha.redis.GoodsKey;
import com.de.miaosha.redis.OrderKey;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.vo.GoodsVo;

@Service
public class OrderService {

	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
		
		//return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}
	
	public OrderInfo getOrderById(Long id) {
		return orderDao.getOrderById(id);
	}

	
	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {

		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);
		
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		//redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
		
		return orderInfo;
	}

}
