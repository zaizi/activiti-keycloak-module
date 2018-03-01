package com.zaizi.authentication;

import com.activiti.extension.bean.KeyCloakEnabled;
import com.zaizi.authentication.CustomizeDaoAuthenticationProvider;
import com.zaizi.authentication.KeyCloakAuthenticationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KeyCloakAuthenticationProviderTest {

	@Spy
	private KeyCloakAuthenticationProvider keyCloakAuthenticationProvider;

	@Mock
	private Environment environment;

	@Mock
	private CustomizeDaoAuthenticationProvider customizeDaoAuthenticationProvider;

	@Mock
	private KeyCloakEnabled keyCloakEnabled;

	@Mock
	private KeycloakBuilder builder;

	@Mock
	private Keycloak client;

	@Mock
	private RealmResource realmsResource;

	@Before
	public void setUp() {

		keyCloakAuthenticationProvider.setKeyCloakEnabled(keyCloakEnabled);
		keyCloakAuthenticationProvider.setCustomizeDaoAuthenticationProvider(customizeDaoAuthenticationProvider);
		keyCloakAuthenticationProvider.setEnvironment(environment);
	}

	/**
	 * Test Retrieve User Details
	 * <p>
	 * {@link KeyCloakAuthenticationProvider#retrieveUser(java.lang.String, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)}
	 */
	@Test
	public void verifyUserRetrieveDetails() {

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		UserDetails stubUserDetails = new User("test", "test", Collections.singletonList(grantedAuthority));
		when(customizeDaoAuthenticationProvider.retrieveUserDetails(Mockito.anyString(),
				Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(stubUserDetails);

		UserDetails userDetails = keyCloakAuthenticationProvider.retrieveUser("test",
				new UsernamePasswordAuthenticationToken("test", "test"));

		assertNotNull(userDetails);

		verify(customizeDaoAuthenticationProvider, times(1)).retrieveUserDetails(Mockito.anyString(),
				Mockito.any(UsernamePasswordAuthenticationToken.class));
	}

	/**
	 * Test the Authentication of the User details
	 * <p>
	 * {@link KeyCloakAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)}
	 */
	@Test
	public void verifyStandardAuthentication() {

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		UserDetails stubUserDetails = new User("test", "test", Collections.singletonList(grantedAuthority));

		when(keyCloakEnabled.isKeyCloakSynchronizeEnabled()).thenReturn(false);

		doNothing().when(customizeDaoAuthenticationProvider).additionalAuthenticationChecks(
				Mockito.any(UserDetails.class), Mockito.any(UsernamePasswordAuthenticationToken.class));

		keyCloakAuthenticationProvider.additionalAuthenticationChecks(stubUserDetails,
				new UsernamePasswordAuthenticationToken("test", "test"));

		assertTrue(true);

		verify(customizeDaoAuthenticationProvider, times(1)).additionalAuthenticationChecks(
				Mockito.any(UserDetails.class), Mockito.any(UsernamePasswordAuthenticationToken.class));

	}

	/**
	 * Test the Authentication of the User details
	 * <p>
	 * {@link KeyCloakAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)}
	 */
	@Test
	public void verifyKeyCloakAuthentication() {

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		UserDetails stubUserDetails = new User("test", "test", Collections.singletonList(grantedAuthority));

		when(keyCloakEnabled.isKeyCloakSynchronizeEnabled()).thenReturn(true);

		when(keyCloakAuthenticationProvider.getKeyCloakBuilder()).thenReturn(builder);

		when(builder.serverUrl(Mockito.anyString())).thenReturn(builder);
		when(builder.realm(Mockito.anyString())).thenReturn(builder);
		when(builder.username(Mockito.anyString())).thenReturn(builder);
		when(builder.password(Mockito.anyString())).thenReturn(builder);
		when(builder.clientId(Mockito.anyString())).thenReturn(builder);
		when(builder.clientSecret(Mockito.anyString())).thenReturn(builder);
		when(builder.grantType(Mockito.anyString())).thenReturn(builder);

		when(builder.build()).thenReturn(client);
		when(client.realm(Mockito.anyString())).thenReturn(realmsResource);
		when(realmsResource.toRepresentation()).thenReturn(new RealmRepresentation());

		keyCloakAuthenticationProvider.additionalAuthenticationChecks(stubUserDetails,
				new UsernamePasswordAuthenticationToken("test", "test"));

		assertTrue(true);

		verify(customizeDaoAuthenticationProvider, times(0)).additionalAuthenticationChecks(
				Mockito.any(UserDetails.class), Mockito.any(UsernamePasswordAuthenticationToken.class));

	}

	/**
	 * Test the Authentication of the User details
	 * <p>
	 * {@link KeyCloakAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)}
	 */
	@Test
	public void verifyKeyCloakAuthenticationFailed() {

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("user");

		UserDetails stubUserDetails = new User("test", "test", Collections.singletonList(grantedAuthority));

		when(keyCloakEnabled.isKeyCloakSynchronizeEnabled()).thenReturn(true);

		when(keyCloakAuthenticationProvider.getKeyCloakBuilder()).thenReturn(builder);

		when(builder.serverUrl(Mockito.anyString())).thenReturn(builder);
		when(builder.realm(Mockito.anyString())).thenReturn(builder);
		when(builder.username(Mockito.anyString())).thenReturn(builder);
		when(builder.password(Mockito.anyString())).thenReturn(builder);
		when(builder.clientId(Mockito.anyString())).thenReturn(builder);
		when(builder.clientSecret(Mockito.anyString())).thenReturn(builder);
		when(builder.grantType(Mockito.anyString())).thenReturn(builder);

		when(builder.build()).thenReturn(client);
		when(client.realm(Mockito.anyString())).thenReturn(realmsResource);
		when(realmsResource.toRepresentation()).thenThrow(new RuntimeException());

		try {

			keyCloakAuthenticationProvider.additionalAuthenticationChecks(stubUserDetails,
					new UsernamePasswordAuthenticationToken("test", "test"));
			fail();
		} catch (AuthenticationException exception) {
			assertTrue(true);
		} catch (Exception exception) {
			fail();
		}

		verify(customizeDaoAuthenticationProvider, times(0)).additionalAuthenticationChecks(
				Mockito.any(UserDetails.class), Mockito.any(UsernamePasswordAuthenticationToken.class));

	}

}
