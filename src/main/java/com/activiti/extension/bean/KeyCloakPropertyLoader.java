package com.activiti.extension.bean;
/**
 * Class is used to Load the Keycloak properties placed in class path
 *
 * @author Vijaya Alla
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:activiti-keycloak.properties")
public class KeyCloakPropertyLoader {


    /**
     * Creates the Property place Holder to load the
     *
     * @return {@code org.springframework.context.support.PropertySourcesPlaceholderConfigurer}
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {

        return new PropertySourcesPlaceholderConfigurer();
    }
}
