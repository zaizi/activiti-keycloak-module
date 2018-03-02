package com.activiti.extension.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class KeyCloakEnabled {

	@Autowired
	private Environment environment;

	/**
	 * Method is used to enable the synchronization of data from Key Cloak.
	 * 
	 * @return {@code Boolean}
	 */
	public Boolean isKeyCloakSynchronizeEnabled() {

		return Boolean.valueOf(environment.getProperty("keycloak.synchronization.enabled", "false"));
	}

}
