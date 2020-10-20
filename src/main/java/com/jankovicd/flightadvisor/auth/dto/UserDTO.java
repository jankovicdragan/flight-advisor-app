package com.jankovicd.flightadvisor.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

	@NotBlank(message = "{user.firstName.required}")
	private String firstName;
	@NotBlank(message = "{user.lastName.required}")
	private String lastName;
	@NotBlank(message = "{user.username.required}")
	private String username;
	@NotBlank(message = "{user.password.required}")
	private String password;

}
