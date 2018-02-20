package com.activiti.extension.bean;
/**
 * Configures KeyCloak with the activiti process engine
 *
 * @author Vijaya Alla
 */

import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfiguration extends AbstractProcessEngineConfigurator {

    @Autowired
    private CustomBean customBean;

    @Bean
    public Keycloak getKeyCloakClient() {


        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(customBean.getPropertyValue("keycloak.auth-server-url"))
                .realm(customBean.getPropertyValue("keycloak.realm"))
                .username(customBean.getPropertyValue("keycloak.userName"))
                .password(customBean.getPropertyValue("keycloak.password"))
                .clientId(customBean.getPropertyValue("keycloak.clientId")).grantType(OAuth2Constants.PASSWORD)
                .clientSecret(customBean.getPropertyValue("keycloak.client.secret")).build();

		RealmResource rm = keycloak.realm(customBean.getPropertyValue("keycloak.realm"));

		/*UsersResource ur = rm.users();

		List<UserRepresentation> urR = ur.list();
		urR.forEach(urreprestn -> {
			System.out.println(String.format("Last Name %s", urreprestn.getLastName()));
		});

		GroupsResource gr = rm.groups();

        gr.groups().forEach(groups -> System.out.println("Get Group Name " + groups.getName()));*/
        return keycloak;
    }


    @Bean
    public KeyCloakUserGroupDetails getKeyCloakUserGroupDetails() {

        return new KeyCloakUserGroupDetails(getKeyCloakClient(), customBean);
    }

}
