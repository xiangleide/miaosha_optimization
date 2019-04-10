package com.de.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.redis.GoodsKey;
import com.de.miaosha.redis.RedisService;
import com.de.miaosha.result.Result;
import com.de.miaosha.service.GoodsService;
import com.de.miaosha.service.MiaoshaUserService;
import com.de.miaosha.vo.GoodsDetailVo;
import com.de.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * QPS:1709 load:15 mysql
	 * 5000*10
	 * QPS:3398,load:5
	 */
	@RequestMapping(value = "/to_list", produces = "text/html")
	@ResponseBody
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		
		// 取缓存
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		// 查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);

		SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap(), applicationContext);
		// 手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}

		return html;
	}

	@RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
	@ResponseBody
	public String toDetail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
			@PathVariable("goodsId") Long goodsId) {
		// 查询商品列表
		model.addAttribute("user", user);

		// 取缓存
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}

		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;

		if (now < startAt) { // 秒杀未开始
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) { // 秒杀已结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else { // 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);

//		return "goods_detail";
		SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap(), applicationContext);
		// 手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		
		return html;
	}
	
	@RequestMapping(value = "/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> toDetail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
			@PathVariable("goodsId") Long goodsId) {

		System.out.println("收到请求");
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		
		int miaoshaStatus = 0;
		int remainSeconds = 0;

		if (now < startAt) { // 秒杀未开始
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) { // 秒杀已结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else { // 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setMiaoshaStatus(miaoshaStatus);
		vo.setRemainSeconds(remainSeconds);
		return Result.success(vo);
	}
}
