package com.activiti.extension.bean;

import org.activiti.app.web.CustomAntPathMatcher;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class CustomAuthenticationConfig<H extends HttpSecurityBuilder<H>> extends
		AbstractAuthenticationFilterConfigurer<H, CustomAuthenticationConfig<H>,
																NoJwtUsernamePasswordAuthenticationFilter> {

	public CustomAuthenticationConfig() {
		super(new NoJwtUsernamePasswordAuthenticationFilter(), (String)null);
		((NoJwtUsernamePasswordAuthenticationFilter)this.getAuthenticationFilter()).setPasswordParameter("username");
		((NoJwtUsernamePasswordAuthenticationFilter)this.getAuthenticationFilter()).setPasswordParameter("password");
	}

	public CustomAuthenticationConfig<H> usernameParameter(String usernameParameter) {
		((NoJwtUsernamePasswordAuthenticationFilter)this.getAuthenticationFilter()).setUsernameParameter(usernameParameter);
		return this;
	}

	public CustomAuthenticationConfig<H> passwordParameter(String passwordParameter) {
		((NoJwtUsernamePasswordAuthenticationFilter)this.getAuthenticationFilter()).setPasswordParameter(passwordParameter);
		return this;
	}

	public void init(H http) throws Exception {
		super.init(http);
		this.initDefaultLoginFilter(http);
	}

	protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
		return new CustomAntPathMatcher(loginProcessingUrl, "POST");
	}

	private void initDefaultLoginFilter(H http) {
		DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = (DefaultLoginPageGeneratingFilter)
				http.getSharedObject(DefaultLoginPageGeneratingFilter.class);

		if (loginPageGeneratingFilter != null && !this.isCustomLoginPage()) {

			loginPageGeneratingFilter.setFormLoginEnabled(true);

			loginPageGeneratingFilter.setUsernameParameter(((NoJwtUsernamePasswordAuthenticationFilter)
					this.getAuthenticationFilter()).getUsernameParameter());

			loginPageGeneratingFilter.setPasswordParameter(((NoJwtUsernamePasswordAuthenticationFilter)
					this.getAuthenticationFilter()).getPasswordParameter());

			loginPageGeneratingFilter.setLoginPageUrl(this.getLoginPage());

			loginPageGeneratingFilter.setFailureUrl(this.getFailureUrl());

			loginPageGeneratingFilter.setAuthenticationUrl(this.getLoginProcessingUrl());

		}

	}
}
