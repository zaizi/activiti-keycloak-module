package com.activiti.extension.config;

import com.activiti.api.security.AlfrescoApiSecurityOverride;
import com.activiti.extension.bean.JwtAuthenticationEntryPoint;
import com.activiti.extension.bean.JwtAuthenticationTokenFilter;
import com.activiti.extension.bean.KeyCloakEnabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Order
public class ActivitiRestSecurityOverride  implements AlfrescoApiSecurityOverride {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private Environment environment;

    @Autowired
    private KeyCloakEnabled keyCloakEnabled;

    @Autowired
	private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;


    /**
     * Decode public key for Keycloak
     * @return {@code java.security.PublicKey}
     * @throws NoSuchAlgorithmException : When No Such algorithm exist
     * @throws InvalidKeySpecException : When Given key is Invalid
     */
    @Bean
    public PublicKey decodePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        X509EncodedKeySpec spec = new X509EncodedKeySpec(pemToDer(this.environment.getProperty("keycloak.client.public.key")));

        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(spec);
    }

    /**
     * Decode a PEM string to DER format
     *
     * @param pem : Public Key which is PEM String
     * @return {@code byte} : In DER Format
     */
    private  byte[] pemToDer(String pem)  {
        return Base64.getDecoder().decode(stripBeginEnd(pem));
    }

	/**
	 * Strip if anything Begin and End found in the token and also new line
	 * @param pem : Token in PEM format
	 * @return {@code String}
	 */
	private  String stripBeginEnd(String pem) {

        String stripped = pem.replaceAll("-----BEGIN (.*)-----", "");
        stripped = stripped.replaceAll("-----END (.*)----", "");
        stripped = stripped.replaceAll("\r\n", "");
        stripped = stripped.replaceAll("\n", "");

        return stripped.trim();
    }


	/**
	 * Configuring HTTP Security
	 * @param httpSecurity : HttpSecurity
	 * @throws Exception : Exception is raised
	 */
	@Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

       if(keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

           jwtAuthenticationTokenFilter.setPublicKey(decodePublicKey());
		   ((HttpSecurity) ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) ((HttpSecurity) ((HttpSecurity)
				   httpSecurity
				   .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				   .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and())
				   .csrf().disable()).antMatcher(this.environment.getProperty("keycloak.api.uri.pattern"))
				   .authorizeRequests().antMatchers(new String[]{this.environment.getProperty("keycloak.api.uri.pattern")}))
				   .authenticated().and())
				   .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
	   } else {

		   ((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)((HttpSecurity)
				   httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and())
				   .csrf().disable()).antMatcher("/api/**").authorizeRequests().antMatchers(new String[]{"/api/**"}))
				   .authenticated().and()).httpBasic();

	   }


    }
}
