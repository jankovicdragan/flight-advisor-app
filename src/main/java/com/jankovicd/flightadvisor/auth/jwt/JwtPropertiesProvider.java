package com.jankovicd.flightadvisor.auth.jwt;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtPropertiesProvider {
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.cookie.name}")
	private String cookieName;

	public SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String getCookieName() {
		return cookieName;
	}
}
