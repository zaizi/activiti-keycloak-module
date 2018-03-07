package com.activiti.extension.bean;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {


    private static final long serialVersionUID = 2816231429144779807L;

    @Override
    public void commence(javax.servlet.http.HttpServletRequest httpServletRequest,
                         javax.servlet.http.HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {

        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

    }
}
