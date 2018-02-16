package com.activiti.extension.bean;
/**
 * Configures KeyCloak with the activiti process engine
 *
 * @author Vijaya Alla
 */

import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.activiti.engine.impl.IdentityServiceImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KeyCloakConfiguration extends AbstractProcessEngineConfigurator {

	@Autowired
	private CustomBean customBean;
	
	@Autowired
	private IdentityServiceImpl identityServiceImpl;

	@Bean
	public Keycloak getKeyCloakClient() {

		/*
		 * Keycloak.getInstance(customBean.getPropertyValue(
		 * "keycloak.auth-server-url"),
		 * customBean.getPropertyValue("keycloak.realm"),
		 * customBean.getPropertyValue("keycloak.realm"),
		 * customBean.getPropertyValue("keycloak.password"),
		 * customBean.getPropertyValue("keycloak.clientId"));
		 */

		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(customBean.getPropertyValue("keycloak.auth-server-url"))
				.realm(customBean.getPropertyValue("keycloak.realm"))
				.username(customBean.getPropertyValue("keycloak.userName"))
				.password(customBean.getPropertyValue("keycloak.password"))
				.clientId(customBean.getPropertyValue("keycloak.clientId")).grantType(OAuth2Constants.PASSWORD)
				.clientSecret(customBean.getPropertyValue("keycloak.client.secret")).build();

		RealmResource rm = keycloak.realm(customBean.getPropertyValue("keycloak.realm"));

		UsersResource ur = rm.users();

		List<UserRepresentation> urR = ur.list();
		urR.forEach(urreprestn -> {
			System.out.println(String.format("Last Name %s", urreprestn.getLastName()));
		});

		return keycloak;

	}

	public RealmResource getRealm() {
		return getKeyCloakClient().realm(customBean.getPropertyValue("keycloak.realm"));
	}

}
