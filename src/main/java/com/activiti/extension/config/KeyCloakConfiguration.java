package com.activiti.extension.config;

import com.activiti.extension.bean.KeyCloakEnabled;
import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KeyCloakConfiguration extends AbstractProcessEngineConfigurator {

	@Autowired
	private Environment environment;

	@Autowired
	private KeyCloakEnabled keyCloakEnabled;

	/**
	 * Creates the Key Cloak Client with the credentials provider
	 * 
	 * @return {@code org.keycloak.admin.client.Keycloak}
	 */
	@Bean
	public Keycloak getKeyCloakClient() {

		if (!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

			return null;
		}

		return KeycloakBuilder.builder().serverUrl(environment.getProperty("keycloak.auth-server-url"))
				.realm(environment.getProperty("keycloak.realm")).username(environment.getProperty("keycloak.userName"))
				.password(environment.getProperty("keycloak.password"))
				.clientId(environment.getProperty("keycloak.clientId")).grantType(OAuth2Constants.PASSWORD)
				.clientSecret(environment.getProperty("keycloak.client.secret")).build();
	}
}
