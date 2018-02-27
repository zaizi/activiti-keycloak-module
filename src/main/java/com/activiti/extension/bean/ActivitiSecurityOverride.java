package com.activiti.extension.bean;

import com.activiti.api.security.AlfrescoSecurityConfigOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ActivitiSecurityOverride  implements AlfrescoSecurityConfigOverride {

    @Autowired
    private KeyCloakEnabled keyCloakEnabled;

    @Autowired
    private Environment environment;


    @Override
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService) {

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
