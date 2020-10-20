package com.jankovicd.flightadvisor.auth.jwt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.util.Bundle;

import io.jsonwebtoken.Jwts;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtPropertiesProvider propertiesProvider;

	public AuthenticationFilter(AuthenticationManager authenticationManager, JwtPropertiesProvider propertiesProvider) {
		this.authenticationManager = authenticationManager;
		this.propertiesProvider = propertiesProvider;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			AuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(),
					AuthenticationRequest.class);
			Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
					authenticationRequest.getPassword());
			return authenticationManager.authenticate(authentication);
		} catch (IOException e) {
			throw new FlightAdvisorException(Bundle.getBundleMessage("authentication.failed"));
		}
	}

	@Override
	public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		String token = Jwts.builder().setSubject(authentication.getName())
				.claim("authorities", authentication.getAuthorities()).setIssuedAt(new Date())
				.setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
				.signWith(propertiesProvider.getSecretKey()).compact();

		Cookie cookie = new Cookie("auth-cookie", token);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

}
