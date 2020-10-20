package com.jankovicd.flightadvisor.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.jankovicd.flightadvisor.auth.handler.ApplicationLogoutHandler;
import com.jankovicd.flightadvisor.auth.jwt.AuthenticationFilter;
import com.jankovicd.flightadvisor.auth.jwt.JwtPropertiesProvider;
import com.jankovicd.flightadvisor.auth.jwt.TokenVerifierFilter;
import com.jankovicd.flightadvisor.auth.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtPropertiesProvider propertiesProvider;

	public ApplicationSecurityConfig(UserService userService, PasswordEncoder passwordEncoder,
			JwtPropertiesProvider propertiesProvider) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.propertiesProvider = propertiesProvider;
	}

	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.addFilter(new AuthenticationFilter(authenticationManager(), propertiesProvider))
			.addFilterAfter(new TokenVerifierFilter(propertiesProvider), AuthenticationFilter.class)
			.authorizeRequests()
				.antMatchers("/auth/register", "/login", "/h2-console/**").permitAll()
				.anyRequest().authenticated()
			.and()
			.logout()
				.deleteCookies(propertiesProvider.getCookieName())
				.logoutSuccessHandler(logoutSuccessHandler())
			.and()
			.headers()	//h2 db
				.frameOptions()
				.disable();
		// @formatter:on
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(userService);
		return provider;
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new ApplicationLogoutHandler();
	}

}
