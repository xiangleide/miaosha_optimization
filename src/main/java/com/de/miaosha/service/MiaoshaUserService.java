package com.de.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.de.miaosha.dao.MiaoshaUserDao;
import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.exception.GlobalException;
import com.de.miaosha.redis.MiaoshaUserKey;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.result.CodeMsg;
import com.de.miaosha.util.MD5Util;
import com.de.miaosha.util.UUIDUtil;
import com.de.miaosha.vo.LoginVo;


@Service
public class MiaoshaUserService {

	public static final String COOKI_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(Long id) {
		//取缓存
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if (user!=null) {
			return user;
		}
		
		//取数据库
		user = miaoshaUserDao.getById(id);
		if (user!=null) {
			redisService.set(MiaoshaUserKey.getById, ""+id, user);
		} 
		return user;
	}

	public boolean updatePassword(String token, long id,String passwordNew) {
		//取user
		MiaoshaUser user = getById(id);
		if (user ==null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//更新数据库
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDbPass(passwordNew, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token, token, user);
		return true;
	}

	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//延长token过期时间
		if (user!=null) {
			addCookie(response, token,user);	
		}
		return user;
	}
	
	/**
	 * login function
	 * @param response
	 * @param loginVo
	 * @return
	 * @throws GlobalException
	 */
	public String login(HttpServletResponse response, LoginVo loginVo) throws GlobalException{
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String password = loginVo.getPassword();
		
		//判断手机号是否存在
		MiaoshaUser mUser = getById(Long.parseLong(mobile));
		if (mUser == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//验证密码
		String dbPass = mUser.getPassword();
		String saltDb = mUser.getSalt();
		String calcPass =  MD5Util.formPassToDbPass(password, saltDb);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//生成cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token,mUser);
		return token;
	}
	
	private void addCookie(HttpServletResponse response,String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

}
