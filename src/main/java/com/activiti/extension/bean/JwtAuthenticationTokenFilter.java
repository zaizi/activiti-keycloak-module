package com.activiti.extension.bean;

import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;

@Component
@Order(1)
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private Environment environment;


    public JwtAuthenticationTokenFilter() {
    }

    public void setPublicKey(PublicKey publicKey) {

    	jwtTokenUtil.setDecodePublicKey(publicKey);
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

    	final String requestHeader = getJWTToken(request);

        String username;
        String authToken;
        if (requestHeader != null && (requestHeader.startsWith("Bearer ") || requestHeader.startsWith("bearer "))) {

            authToken = requestHeader.substring(7);

            try {

                username = jwtTokenUtil.verifyAndGetUserName(authToken);

            } catch (IllegalArgumentException exception) {

                logger.error("an error occured during getting username from token", exception);
                throw new InternalAuthenticationServiceException("Invalid JWT Token", exception);
            } catch (ExpiredJwtException e) {

                logger.warn("the token is expired and not valid anymore", e);
                throw new CredentialsExpiredException("Token Expired", e);
            }

            logger.info("checking authentication for user " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } else {

            logger.warn("couldn't find bearer string, will ignore the header");
        }


        chain.doFilter(request, response);
    }


    /**
     * Retrieve the JWT token from the request. It starts check at Header , Then Parameter and then in Attribute
     * @param request : HttpServletRequest
     * @return {@code String}
     */
    private String getJWTToken(HttpServletRequest request) {
        String jwtToken;


        jwtToken = request.getHeader(this.environment.getProperty("keycloak.api.authentication.header"));

        if(StringUtils.isEmpty(jwtToken)) {
            jwtToken = request.getParameter(this.environment.getProperty("keycloak.api.authentication.header"));
        }

        if(StringUtils.isEmpty(jwtToken)) {

            Object token = request.getAttribute(this.environment.getProperty("keycloak.api.authentication.header"));
            if(token != null) {
                jwtToken = (String) token;
            }
        }

        return jwtToken;
    }
}