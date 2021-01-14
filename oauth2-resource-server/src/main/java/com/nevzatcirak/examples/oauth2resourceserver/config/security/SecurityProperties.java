package com.nevzatcirak.examples.oauth2resourceserver.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "rest.security")
public class SecurityProperties {
	private boolean enabled;
	private String apiMatcher;
	private Cors cors;
	private String issuerUri;

	public CorsConfiguration getCorsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(this.cors.getAllowedOrigins());
		corsConfiguration.setAllowedMethods(this.cors.getAllowedMethods());
		corsConfiguration.setAllowedHeaders(this.cors.getAllowedHeaders());
		corsConfiguration.setExposedHeaders(this.cors.getExposedHeaders());
		corsConfiguration.setAllowCredentials(this.cors.getAllowCredentials());
		corsConfiguration.setMaxAge(this.cors.getMaxAge());

		return corsConfiguration;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getApiMatcher() {
		return this.apiMatcher;
	}

	public void setApiMatcher(String apiMatcher) {
		this.apiMatcher = apiMatcher;
	}

	public Cors getCors() {
		return this.cors;
	}

	public void setCors(Cors cors) {
		this.cors = cors;
	}

	public String getIssuerUri() {
		return this.issuerUri;
	}

	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}
	
}
