package com.activiti.extension.config;

import com.activiti.api.security.AlfrescoSecurityConfigOverride;
import com.activiti.extension.bean.KeyCloakEnabled;
import com.zaizi.authentication.CustomizeDaoAuthenticationProvider;
import com.zaizi.authentication.KeyCloakAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
public class ActivitiSecurityOverride implements AlfrescoSecurityConfigOverride {

	@Autowired
	private KeyCloakEnabled keyCloakEnabled;

	@Autowired
	private Environment environment;

	@Override
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder,
			UserDetailsService userDetailsService) {

		KeyCloakAuthenticationProvider authenticationProvider = new KeyCloakAuthenticationProvider();
		authenticationProvider.setEnvironment(environment);
		authenticationProvider.setKeyCloakEnabled(keyCloakEnabled);
		CustomizeDaoAuthenticationProvider customizeDaoAuthenticationProvider = new CustomizeDaoAuthenticationProvider();
		customizeDaoAuthenticationProvider.setUserDetailsService(userDetailsService);
		customizeDaoAuthenticationProvider.setPasswordEncoder(new StandardPasswordEncoder());
		authenticationProvider.setCustomizeDaoAuthenticationProvider(customizeDaoAuthenticationProvider);
		authenticationManagerBuilder.authenticationProvider(authenticationProvider);
	}
}
