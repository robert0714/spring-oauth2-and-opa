
package com.nevzatcirak.examples.oauth2resourceserver.config.security;


 import com.nevzatcirak.examples.oauth2resourceserver.config.security.GrantedAuthoritiesExtractor;
 import com.nevzatcirak.examples.oauth2resourceserver.config.security.SecurityProperties;
 import java.util.Objects;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Import;
 import org.springframework.core.convert.converter.Converter;
 import org.springframework.security.authentication.AbstractAuthenticationToken;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
 import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.oauth2.jwt.Jwt;
 import org.springframework.security.oauth2.jwt.JwtDecoder;
 import org.springframework.security.oauth2.jwt.JwtDecoders;
 import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
 import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
 import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
 import org.springframework.web.cors.CorsConfigurationSource;
 import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

	@EnableWebSecurity
	@Import({ SecurityProperties.class })
	public class ResourceServerSecurityConfig extends WebSecurityConfigurerAdapter {
	 private String issuerUri;
	 private String jwkSetUri;
	 @Autowired
	 private SecurityProperties securityProperties;

	
	 public ResourceServerSecurityConfig(@Value("${auth.issuer-uri}") String issuerUri,
			@Value("${auth.jwk-set-uri}") String jwkSetUri, @Value("${auth.host}") String hostname) {
		 String authHostname = System.getenv("AUTH_HOSTNAME");
		 if (!Objects.isNull(authHostname)) {
			 this.jwkSetUri = jwkSetUri.replaceAll(hostname, authHostname);
			 this.issuerUri = issuerUri.replaceAll(hostname, authHostname);
			 } else {
			 this.jwkSetUri = jwkSetUri;
			 this.issuerUri = issuerUri;
			 }
		 }	
	 protected void configure(HttpSecurity http) throws Exception {
     ((HttpSecurity)((HttpSecurity)http
       .cors()
       .configurationSource(corsConfigurationSource())
       .and())
       .sessionManagement()
       .sessionAuthenticationStrategy((SessionAuthenticationStrategy)new NullAuthenticatedSessionStrategy())
       .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
       .and())
       .authorizeRequests(authorizeRequests -> ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)authorizeRequests.anyRequest()).authenticated())
     .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt( ));
   }	
	
	@Bean
	public JwtDecoder jwtDecoder() {
		return JwtDecoders.fromIssuerLocation(this.issuerUri);
	}

	@Bean
	Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractorConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter((Converter) grantedAuthoritiesExtractor());
		return (Converter<Jwt, AbstractAuthenticationToken>) jwtAuthenticationConverter;
	}

	@Bean
	GrantedAuthoritiesExtractor grantedAuthoritiesExtractor() {
		return new GrantedAuthoritiesExtractor();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		if (null != this.securityProperties.getCorsConfiguration()) {

			source.registerCorsConfiguration("/**", this.securityProperties.getCorsConfiguration());
		}
		return (CorsConfigurationSource) source;
	}
}