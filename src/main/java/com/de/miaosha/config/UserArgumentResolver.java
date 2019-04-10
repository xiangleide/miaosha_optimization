package com.de.miaosha.config;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.de.miaosha.domain.MiaoshaUser;
import com.de.miaosha.service.MiaoshaUserService;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserArgumentResolver.class);
	@Autowired
	MiaoshaUserService userService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return MiaoshaUser.class == clazz;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

		String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
		Cookie[] cookies = request.getCookies();
		String cookieToken = null;
		if (cookies != null && cookies.length > 0) {
			Optional<Cookie> optional = Arrays.stream(cookies)
					.filter(c -> c.getName().equals(MiaoshaUserService.COOKI_NAME_TOKEN)).findFirst();
			cookieToken = optional.get().getValue();
		}

		if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		LOGGER.info("token:{}", token);
		return userService.getByToken(response, token);
	}

}
