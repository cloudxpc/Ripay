package com.rickxpc.ripay.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);
	
  	@Override
  	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
  			throws Exception {
        logger.info("do interceptor");
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
        	//throw new TokenNotFoundException("Token not found");
        }
        
        //validate token from wechat public platform
        if (!token.equals("123")) {
        	//throw new InvalidTokenException("Token not invalid");
        }
        
        
  		return true;
  	}
}
