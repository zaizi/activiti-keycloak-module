package com.zaizi.authentication;

import com.activiti.extension.bean.KeyCloakEnabled;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class KeyCloakAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private final Logger LOGGER = LoggerFactory.getLogger(KeyCloakAuthenticationProvider.class);

	private Environment environment;

	private CustomizeDaoAuthenticationProvider customizeDaoAuthenticationProvider;

	private KeyCloakEnabled keyCloakEnabled;

	/**
	 * If Key Cloak is Enabled Authentication is done through the Key Cloak ,
	 * else it follows the DAO Authentication
	 *
	 * @param userDetails
	 *            : UserDetails object which holds user details trying to login
	 * @param usernamePasswordAuthenticationToken
	 *            : User Name and Password details
	 * @throws AuthenticationException
	 *             : if failed, throws a Authentication exception.
	 */

	@Override
	public void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

		if (!keyCloakEnabled.isKeyCloakRealmLoginEnabled()) {

			customizeDaoAuthenticationProvider.additionalAuthenticationChecks(userDetails,
					usernamePasswordAuthenticationToken);
			return;
		}

		try {

			Keycloak keycloak = getKeyCloakBuilder().serverUrl(environment.getProperty("keycloak.auth-server-url.to.login.in.realm"))
					.realm(environment.getProperty("keycloak.realm.to.login"))
					.username(usernamePasswordAuthenticationToken.getName())
					.password(usernamePasswordAuthenticationToken.getCredentials().toString())
					.clientId(environment.getProperty("keycloak.realm.clientId")).grantType(OAuth2Constants.PASSWORD)
					.clientSecret(environment.getProperty("keycloak.realm.client.secret")).build();

			keycloak.realm(environment.getProperty("keycloak.realm.to.login")).toRepresentation();
		} catch (Exception exception) {
			LOGGER.error("Authentication Failed", exception);
			throw new BadCredentialsException("Authentication failed  ", exception);
		}

	}

	/**
	 * Retrieves the User details from the Activiti Alfresco database
	 *
	 * @param username
	 *            : User Name entered in the User Interface
	 * @param usernamePasswordAuthenticationToken
	 *            : User Name and Password details
	 * @return {@code org.springframework.security.core.userdetails.UserDetails }
	 * @throws AuthenticationException
	 *             : An Exception is thrown if the User not found in the
	 *             Activiti
	 */
	@Override
	public UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

		return customizeDaoAuthenticationProvider.retrieveUserDetails(username, usernamePasswordAuthenticationToken);
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setCustomizeDaoAuthenticationProvider(
			CustomizeDaoAuthenticationProvider customizeDaoAuthenticationProvider) {
		this.customizeDaoAuthenticationProvider = customizeDaoAuthenticationProvider;
	}

	public void setKeyCloakEnabled(KeyCloakEnabled keyCloakEnabled) {
		this.keyCloakEnabled = keyCloakEnabled;
	}

	public KeycloakBuilder getKeyCloakBuilder() {

		return KeycloakBuilder.builder();
	}
}
