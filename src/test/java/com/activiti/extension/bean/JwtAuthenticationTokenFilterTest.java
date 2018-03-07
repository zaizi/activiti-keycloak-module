package com.activiti.extension.bean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationTokenFilterTest {

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private JwtTokenUtil jwtTokenUtil;

	@Mock
	private Environment environment;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private HttpServletResponse httpServletResponse;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;


	private String token = "bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2blh6SDF5ZVM4eDk0R1dnV3FIcUxmOGlxSUhNWmd0Y2QzVWdYWGtBMVFJIn0.eyJqdGkiOiIwYzZmMTk1Yi0yYTFjLTQyYzUtYmM1MC1jMzU3ZDhiYzJlOGUiLCJleHAiOjE1MjAyNjY3ODcsIm5iZiI6MCwiaWF0IjoxNTIwMjY2MTg3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImEwOTRlZjAxLTNkNTctNGU0Mi1hZTUyLWM2NDE3MmRkYTA2ZCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6IjhjNjk5YjRkLTI3NjMtNDM2Yy04NWY4LWY0M2YzZjcxNDM0ZSIsImF1dGhfdGltZSI6MTUyMDI2NjE4Niwic2Vzc2lvbl9zdGF0ZSI6IjA3OGRiM2RkLTAyZTQtNDM0YS1iZDM5LWVjYmYzYWZlY2MzOSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiVGVzdCBKYWNvYiIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIiLCJnaXZlbl9uYW1lIjoiVGVzdCIsImZhbWlseV9uYW1lIjoiSmFjb2IifQ.CnZFbeKRMhrZpC6qwN6PEuUCxPSbtLi8i7H_95P-EIhXSwVUB723ZUqnD6XqpiCSBQ88-XUw9MCmFr0g5lTSTucGQPnW9qLkZc_Y6Z1GYsu7SVbqYxYqG0K4lbGjjeiOCpUSTMI_Y_H6ZA2zejJHa6ce_bEzAMmYsJFQTWZeBRxBe_2q31u6SpW9wZnkPKwwVI8rjyOWunTertptysnAKgf1yPYGKCo61p7GVcAgOi5I2wM4LPXzjYi1khM9PCrE-YlQ6WGWUFv9150RpeJD9Sv8DFHQk8kDjmjMemHtmK9yFLQtz3TA04LzAcvg1wTz03-iCH5GpaoZRrf3GqxBkQ";

	@Before
	public void init() {
		jwtAuthenticationTokenFilter.setUserDetailsService(userDetailsService);
		jwtAuthenticationTokenFilter.setJwtTokenUtil(jwtTokenUtil);
		jwtAuthenticationTokenFilter.setEnvironment(environment);
	}


	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestNoJWTToken() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(jwtTokenUtil, times(0)).verifyAndGetUserName(Mockito.anyString());
	}

	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestNoBearer() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn("test");
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(jwtTokenUtil, times(0)).verifyAndGetUserName(Mockito.anyString());
	}

	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestNoBearerInParameter() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn("test");
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(jwtTokenUtil, times(0)).verifyAndGetUserName(Mockito.anyString());
	}

	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestNoBearerInAttribute() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn("test");

		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(jwtTokenUtil, times(0)).verifyAndGetUserName(Mockito.anyString());
	}


	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestNoUserNameFound() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(token);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		when(jwtTokenUtil.verifyAndGetUserName(Mockito.anyString())).thenReturn(null);
		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(userDetailsService, times(0)).loadUserByUsername(Mockito.anyString());
		verify(jwtTokenUtil, times(1)).verifyAndGetUserName(Mockito.anyString());
	}


	public void mockApplicationUser() {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new User("test",
				"test", Collections.singletonList(grantedAuthority)));
	}

	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequest() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(token);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		when(jwtTokenUtil.verifyAndGetUserName(Mockito.anyString())).thenReturn("test");
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(new User("test",
				"test", Collections.singletonList(grantedAuthority)));
		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(userDetailsService, times(1)).loadUserByUsername(Mockito.anyString());
		verify(jwtTokenUtil, times(1)).verifyAndGetUserName(Mockito.anyString());
	}


	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test
	public void noTokenSendInRequestAlreadyAuthenticated() throws ServletException, IOException {

		mockApplicationUser();
		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(token);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		when(jwtTokenUtil.verifyAndGetUserName(Mockito.anyString())).thenReturn("test");
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(new User("test",
				"test", Collections.singletonList(grantedAuthority)));
		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(userDetailsService, times(0)).loadUserByUsername(Mockito.anyString());
		verify(jwtTokenUtil, times(1)).verifyAndGetUserName(Mockito.anyString());
	}


	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test(expected = InternalAuthenticationServiceException.class)
	public void noTokenSendInRequesttokenVerifyFail() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(token);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		when(jwtTokenUtil.verifyAndGetUserName(Mockito.anyString())).thenThrow(new IllegalArgumentException());
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(new User("test",
				"test", Collections.singletonList(grantedAuthority)));
		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(userDetailsService, times(0)).loadUserByUsername(Mockito.anyString());
		verify(jwtTokenUtil, times(1)).verifyAndGetUserName(Mockito.anyString());
	}

	/**
	 * Test doFilter {@link JwtAuthenticationTokenFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
	 */
	@Test(expected = CredentialsExpiredException.class)
	public void noTokenSendInRequesttokenVerifyExpired() throws ServletException, IOException {

		doReturn("Authorization").when(environment).getProperty(Mockito.anyString());

		when(httpServletRequest.getHeader(Mockito.anyString())).thenReturn(token);
		when(httpServletRequest.getParameter(Mockito.anyString())).thenReturn(null);
		when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(null);

		when(jwtTokenUtil.verifyAndGetUserName(Mockito.anyString())).thenThrow(new CredentialsExpiredException("Expired"));
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(new User("test",
				"test", Collections.singletonList(grantedAuthority)));
		jwtAuthenticationTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		verify(userDetailsService, times(0)).loadUserByUsername(Mockito.anyString());
		verify(jwtTokenUtil, times(1)).verifyAndGetUserName(Mockito.anyString());
	}
}
