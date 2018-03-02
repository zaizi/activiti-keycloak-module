package com.activiti.extension.config;

import com.activiti.api.security.AlfrescoApiSecurityExtender;
import com.activiti.api.security.AlfrescoApiSecurityOverride;
import com.activiti.extension.bean.JwtAuthenticationEntryPoint;
import com.activiti.extension.bean.JwtAuthenticationTokenFilter;
import com.activiti.extension.bean.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ActivitiRestSecurityOverride  implements AlfrescoApiSecurityOverride {

    //implements AlfrescoApiSecurityOverride
    //



    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Environment environment;






    @Bean
    public JwtAuthenticationTokenFilter doGetAuthenticationFilter() {

        JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
        jwtAuthenticationTokenFilter.setEnvironment(environment);
        jwtAuthenticationTokenFilter.setJwtTokenUtil(jwtTokenUtil);
        jwtAuthenticationTokenFilter.setUserDetailsService(userDetailsService);
        ///jwtAuthenticationTokenFilter.setUsernamePasswordAuthenticationFilter(usernamePasswordAuthenticationFilter);
        return jwtAuthenticationTokenFilter;
    };


    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

/*        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()

                .antMatchers("/api/**").authenticated();*/

        httpSecurity
             //   .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .authorizeRequests()
                .antMatchers("/api/**")
                .authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(doGetAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

  /*      // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();*/
    }
}
