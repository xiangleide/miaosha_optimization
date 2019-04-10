package com.de.miaosha.redis;

public class GoodsKey extends BasePrefix{

	public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
	public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
	public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"gs");
	
	public GoodsKey(int expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}

}
