package com.jankovicd.flightadvisor.auth.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.util.Bundle;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class TokenVerifierFilter extends OncePerRequestFilter {

	private final JwtPropertiesProvider propertiesProvider;

	public TokenVerifierFilter(JwtPropertiesProvider propertiesProvider) {
		this.propertiesProvider = propertiesProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Cookie cookie = WebUtils.getCookie(request, "auth-cookie");

		if (cookie == null) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			String token = cookie.getValue();
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(propertiesProvider.getSecretKey()).build()
					.parseClaimsJws(token);

			Claims body = claims.getBody();
			String username = body.getSubject();

			@SuppressWarnings("unchecked")
			List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");
			List<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
					.map(m -> new SimpleGrantedAuthority(m.get("authority"))).collect(Collectors.toList());
			Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
					simpleGrantedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (JwtException e) {
			throw new FlightAdvisorException(Bundle.getBundleMessage("token.unverified"));
		}
		filterChain.doFilter(request, response);

	}

}
