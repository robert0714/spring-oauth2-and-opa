package com.nevzatcirak.examples.oauth2resourceserver.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
	@Value("${resourceServer.resourceId}")
	private String resourceId;

	public Collection<GrantedAuthority> convert(Jwt jwt) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		if (jwt.containsClaim("resource_access").booleanValue()) {
			JSONObject resourceAccess = (JSONObject) jwt.getClaims().get("resource_access");
			if (resourceAccess.containsKey(this.resourceId)) {
				JSONArray resourceRoles = (JSONArray) ((JSONObject) resourceAccess.get(this.resourceId)).get("roles");
				for (Object resourceRole : resourceRoles) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" + resourceRole.toString().toUpperCase()));
				}
			}
		}

		if (jwt.containsClaim("realm_access").booleanValue()) {
			JSONObject realmAccess = (JSONObject) jwt.getClaims().get("realm_access");
			if (realmAccess.containsKey("roles")) {
				JSONArray realmRoles = (JSONArray) realmAccess.get("roles");
				for (Object realmRole : realmRoles) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" + realmRole.toString().toUpperCase()));
				}
			}
		}

		if (jwt.containsClaim("scope").booleanValue()) {
			String scope = (String) jwt.getClaims().get("scope");
			if (!scope.isEmpty()) {
				String[] scopes = scope.split("\\s");
				for (String scopeAuthority : scopes) {
					authorities.add(new SimpleGrantedAuthority("SCOPE_" + scopeAuthority.toUpperCase()));
				}
			}
		}

		return authorities;
	}
}
