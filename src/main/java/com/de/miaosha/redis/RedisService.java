package com.de.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
	
	@Autowired
	JedisPool jedisPool;

	/**
	 * 获取对象
	 * @param prefix
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(KeyPrefix prefix,String key,Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			String str = jedis.get(realKey);
			T t =stringToBean(str,clazz);
			return t;
		} finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 添加对象
	 * @param prefix
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> Boolean set(KeyPrefix prefix,String key,T value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String string = beanToString(value);
			if (string == null || string.length()<=0) {
				return false;
			}
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			int seconds = prefix.expireSeconds();
			if (seconds<=0) {
				jedis.set(realKey, string);
			}else {
				//设置过期时间
				jedis.setex(realKey, seconds, string);
			}
			
			return true;
		} finally {
			returnToPool(jedis);
		}
	}
	/**
	 * 判断是否存在
	 * @param prefix
	 * @param key
	 * @return
	 */
	public <T> Boolean exist(KeyPrefix prefix,String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			return jedis.exists(realKey);
		} finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 增加值
	 * @param prefix
	 * @param key
	 * @return
	 */
	public <T> Long incr(KeyPrefix prefix,String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			return jedis.incr(realKey);
		} finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 减少值
	 * @param prefix
	 * @param key
	 * @return
	 */
	public <T> Long decr(KeyPrefix prefix,String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			return jedis.decr(realKey);
		} finally {
			returnToPool(jedis);
		}
	}
	
	/**
	 * 删除
	 * @param prefix
	 * @param key
	 * @return
	 */
	public boolean delete(KeyPrefix prefix,String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//生成真正的key
			String realKey = prefix.getPrefix()+key;
			long result =jedis.del(realKey);
			return result>0;
		} finally {
			returnToPool(jedis);
		}
	}
	
	public static <T> String beanToString(T value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (clazz == int.class||clazz == Integer.class) {
			return ""+value;
		}else if(clazz == String.class){
			return (String)value;
		}else if (clazz == long.class||clazz == Long.class) {
			return ""+value;
		}
		else {
			return JSON.toJSONString(value);	
		}
	}

	public static <T> T stringToBean(String string ,Class<T> clazz) {
		if (string == null||string.length()<=0) {
			return null;
		}
		
		if (clazz == int.class||clazz == Integer.class) {
			return (T)Integer.valueOf(string);
		}else if(clazz == String.class){
			return (T)string;
		}else if (clazz == long.class||clazz == Long.class) {
			return (T)Long.valueOf(string);
		}
		else {
			return JSON.toJavaObject(JSON.parseObject(string), clazz);	
		}
		
	}

	private void returnToPool(Jedis jedis) {

		if (jedis != null) {
			jedis.close();
		}
	}


}
