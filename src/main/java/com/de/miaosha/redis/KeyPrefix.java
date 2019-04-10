package com.de.miaosha.redis;

public interface KeyPrefix {

	public int expireSeconds();
	
	public String getPrefix();
}
