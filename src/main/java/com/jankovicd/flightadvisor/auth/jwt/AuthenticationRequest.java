package com.jankovicd.flightadvisor.auth.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationRequest {

	private String username;
	private String password;

}
