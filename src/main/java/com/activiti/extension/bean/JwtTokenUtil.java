package com.activiti.extension.bean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.PublicKey;

@Component
public class JwtTokenUtil implements Serializable {


    private static final long serialVersionUID = 6143879608320532812L;

    @Autowired
    private Environment environment;

    private PublicKey decodePublicKey;

	/**
	 * Set the Public key to verify and get the User Name
	 * @param decodePublicKey : java.security.PublicKey
	 */
	public void setDecodePublicKey(PublicKey decodePublicKey) {
        this.decodePublicKey = decodePublicKey;
    }

    /**
     * Get the User Name from Token after Sign with the Publick Key
     * @param token : JWT Token
     * @return {@code String} : User name as String
     */
    public String verifyAndGetUserName(String token) {

		final Claims claims = getAllClaimsFromToken(token);

		if (claims.containsKey(this.environment.getProperty("keycloak.client.user.name.key"))) {
			return (String) claims.get(this.environment.getProperty("keycloak.client.user.name.key"));
		}
		return null;
    }


    /**
     * Signature Verify and Retrieve all the claims from Body of the Token
     * @param token : JWT Token
     * @return {@code io.jsonwebtoken.Claims}
     */
    private Claims getAllClaimsFromToken(String token)   {

             return Jwts.parser()
                    .setSigningKey(decodePublicKey)
                    .parseClaimsJws(token)
                    .getBody();
    }

}
