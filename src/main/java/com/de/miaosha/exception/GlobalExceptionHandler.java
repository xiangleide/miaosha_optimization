package com.de.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.de.miaosha.result.CodeMsg;
import com.de.miaosha.result.Result;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	private static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	//全局异常处理
	@ExceptionHandler(value = Exception.class)
	public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
		LOGGER.info("do Exception handler.");
		e.printStackTrace();
		if (e instanceof GlobalException) {
			CodeMsg codeMsg = ((GlobalException) e).getCm();
			return Result.error(codeMsg);
		}else if (e instanceof BindException) {
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			
			String msg = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
		
		
		
		
	}
}
