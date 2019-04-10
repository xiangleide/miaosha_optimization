package com.de.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.de.miaosha.dao.GoodsDao;
import com.de.miaosha.domain.MiaoshaGoods;
import com.de.miaosha.vo.GoodsVo;

@Service
public class GoodsService {

	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVoList();
	}

	public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int result = 0;
		result = goodsDao.reduceStock(g);
		return result>0;
	}
}
