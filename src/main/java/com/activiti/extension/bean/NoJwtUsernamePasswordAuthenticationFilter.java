package com.activiti.extension.bean;

import org.activiti.app.web.CustomUsernamePasswordAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class NoJwtUsernamePasswordAuthenticationFilter extends CustomUsernamePasswordAuthenticationFilter {

	private final Logger log = LoggerFactory.getLogger(NoJwtUsernamePasswordAuthenticationFilter.class);

	public NoJwtUsernamePasswordAuthenticationFilter() {

	}

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		if (SecurityContextHolder.getContext().getAuthentication() != null) {

			return SecurityContextHolder.getContext().getAuthentication();
		}

		try {
			request.setCharacterEncoding("UTF-8");
		} catch (Exception var4) {
			this.log.error("Error setting character encoding to UTF-8");
		}

		return super.attemptAuthentication(request, response);
	}
}
