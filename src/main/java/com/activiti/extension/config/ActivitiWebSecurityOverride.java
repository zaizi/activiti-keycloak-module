package com.activiti.extension.config;

import com.activiti.api.security.AlfrescoWebAppSecurityExtender;
import com.activiti.extension.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.PublicKey;

@Configuration
@Order
public class ActivitiWebSecurityOverride implements AlfrescoWebAppSecurityExtender {


	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;


	@Autowired
	private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

	@Autowired
	private PublicKey decodePublicKey;

	@Autowired
	private KeyCloakEnabled keyCloakEnabled;

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {

		if(keyCloakEnabled.isKeyCloakRealmLoginEnabled()) {


			jwtAuthenticationTokenFilter.setPublicKey(decodePublicKey);

			httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);


			CustomAuthenticationConfig<HttpSecurity> loginConfig = new CustomAuthenticationConfig();
			((CustomAuthenticationConfig)
					((CustomAuthenticationConfig)
							((CustomAuthenticationConfig)
									loginConfig.loginProcessingUrl("/app/authentication"))
									.successHandler(this.authenticationSuccessHandler))
							.failureHandler(this.authenticationFailureHandler))
					.usernameParameter("j_username")
					.passwordParameter("j_password")
					.permitAll();
			httpSecurity.apply(loginConfig);
		}
	}
}
