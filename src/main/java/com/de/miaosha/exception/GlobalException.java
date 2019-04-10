package com.de.miaosha.exception;

import com.de.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private CodeMsg cm;
	
	public GlobalException(CodeMsg cm) {
		this.cm = cm;
	}

	public CodeMsg getCm() {
		return cm;
	}
	
}
