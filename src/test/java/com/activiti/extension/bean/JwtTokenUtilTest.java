package com.activiti.extension.bean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenUtilTest {

	@Spy
	private JwtTokenUtil jwtTokenUtil;

	@Mock
	private Environment environment;


	private String token = "bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2blh6SDF5ZVM4eDk0R1dnV3FIcUxmOGlxSUhNWmd0Y2QzVWdYWGtBMVFJIn0.eyJqdGkiOiIwYzZmMTk1Yi0yYTFjLTQyYzUtYmM1MC1jMzU3ZDhiYzJlOGUiLCJleHAiOjE1MjAyNjY3ODcsIm5iZiI6MCwiaWF0IjoxNTIwMjY2MTg3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImEwOTRlZjAxLTNkNTctNGU0Mi1hZTUyLWM2NDE3MmRkYTA2ZCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6IjhjNjk5YjRkLTI3NjMtNDM2Yy04NWY4LWY0M2YzZjcxNDM0ZSIsImF1dGhfdGltZSI6MTUyMDI2NjE4Niwic2Vzc2lvbl9zdGF0ZSI6IjA3OGRiM2RkLTAyZTQtNDM0YS1iZDM5LWVjYmYzYWZlY2MzOSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiVGVzdCBKYWNvYiIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIiLCJnaXZlbl9uYW1lIjoiVGVzdCIsImZhbWlseV9uYW1lIjoiSmFjb2IifQ.CnZFbeKRMhrZpC6qwN6PEuUCxPSbtLi8i7H_95P-EIhXSwVUB723ZUqnD6XqpiCSBQ88-XUw9MCmFr0g5lTSTucGQPnW9qLkZc_Y6Z1GYsu7SVbqYxYqG0K4lbGjjeiOCpUSTMI_Y_H6ZA2zejJHa6ce_bEzAMmYsJFQTWZeBRxBe_2q31u6SpW9wZnkPKwwVI8rjyOWunTertptysnAKgf1yPYGKCo61p7GVcAgOi5I2wM4LPXzjYi1khM9PCrE-YlQ6WGWUFv9150RpeJD9Sv8DFHQk8kDjmjMemHtmK9yFLQtz3TA04LzAcvg1wTz03-iCH5GpaoZRrf3GqxBkQ";

	@Before
	public void setUp() {

		jwtTokenUtil.setEnvironment(environment);
	}

	/**
	 * Test Jwt Token Util
	 * {@link JwtTokenUtil#verifyAndGetUserName(java.lang.String)}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyAndUserNameNotValidSignature() {

		doThrow(new IllegalArgumentException()).when(jwtTokenUtil).getAllClaimsFromToken(Mockito.anyString());
		when(environment.getProperty(Mockito.anyString())).thenReturn("test");
		jwtTokenUtil.verifyAndGetUserName(token);
	}


	/**
	 * Test Jwt Token Util
	 * {@link JwtTokenUtil#verifyAndGetUserName(java.lang.String)}
	 */
	@Test
	public void verifyAndUserNameValidSignatureNoUser() {

		doReturn(new DefaultClaims()).when(jwtTokenUtil).getAllClaimsFromToken(Mockito.anyString());
		when(environment.getProperty(Mockito.anyString())).thenReturn("test");
		assertNull(jwtTokenUtil.verifyAndGetUserName(token));
	}

	/**
	 * Test Jwt Token Util
	 * {@link JwtTokenUtil#verifyAndGetUserName(java.lang.String)}
	 */
	@Test
	public void verifyAndUserNameValidSignatureUser() {

		Claims claims = new DefaultClaims();
		claims.put("test", "user");
		doReturn(claims).when(jwtTokenUtil).getAllClaimsFromToken(Mockito.anyString());
		when(environment.getProperty(Mockito.anyString())).thenReturn("test");
		assertEquals("user", jwtTokenUtil.verifyAndGetUserName(token));
	}

}
