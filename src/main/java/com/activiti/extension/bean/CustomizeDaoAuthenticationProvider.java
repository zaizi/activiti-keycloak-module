package com.activiti.extension.bean;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomizeDaoAuthenticationProvider extends DaoAuthenticationProvider {


    /**
     * Wrapper method to access the DAO Authentication method
     * @param userDetails : UserDetails
     * @param authentication : UsernamePasswordAuthenticationToken which holds User name and Password
     * @throws AuthenticationException : Exception on authentication fail
     */
    public void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        super.additionalAuthenticationChecks(userDetails, authentication);
    }


    /**
     * Wrapper class to get the Authentication details
     * @param username : User Name as String
     * @param authentication : UsernamePasswordAuthenticationToken which holds User name and Password
     * @return {@code org.springframework.security.core.userdetails.UserDetails }
     * @throws AuthenticationException : Exception on authentication fail
     */
    public UserDetails retrieveUserDetails(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        return super.retrieveUser(username, authentication);
    }


}
