package com.de.miaosha.result;

public class CodeMsg {

	private int code;
	private String message;
	
	//通用异常5001XX
	public static CodeMsg SUCCESS = new CodeMsg(0,"succcess");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常:%s");
	
	//登陆模块异常5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登陆密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号格式错误");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214,"手机号不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"密码错误");
	//商品模块异常5003XX
	
	//订单模块异常5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");
	
	//秒杀模块异常5005XX
	public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500,"商品已经结束秒杀");
	public static CodeMsg REPEAT_MIAOSHA = new CodeMsg(500501,"商品不能重复秒杀");
	
	
	public CodeMsg(int i, String string) {
		this.code = i;
		this.message = string;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.message, args);
		return new CodeMsg(code, message);
	}
}
