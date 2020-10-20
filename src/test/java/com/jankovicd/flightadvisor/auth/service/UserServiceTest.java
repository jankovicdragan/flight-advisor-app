package com.jankovicd.flightadvisor.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jankovicd.flightadvisor.auth.dto.UserDTO;
import com.jankovicd.flightadvisor.auth.model.User;
import com.jankovicd.flightadvisor.auth.repository.UserRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;

@SpringBootTest(classes = UserService.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	private static final String SOME_ENCODED_VALUE = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGhvcm...";

	private static final String USERNAME = "username";

	@Autowired
	UserService userService;

	@MockBean
	PasswordEncoder passwordEncoder;

	@MockBean
	UserRepository userRepository;

	@Test
	public void loadUserByUsername_usernameDoesntExist_flightAdvisorException() {
		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.ofNullable(null));

		assertThrows(FlightAdvisorException.class, () -> userService.loadUserByUsername(USERNAME));
	}

	@Test
	public void loadUserByUsername_usernameExists_userReturned() {
		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User()));

		assertThat(userService.loadUserByUsername(USERNAME)).isNotNull();
	}

	@Test
	public void register_repositoryMethodIsCalled() {
		when(passwordEncoder.encode(USERNAME)).thenReturn(SOME_ENCODED_VALUE);
		UserDTO user = new UserDTO();
		user.setPassword(USERNAME);
		user.setUsername(USERNAME);
		user.setFirstName(USERNAME);
		user.setLastName(USERNAME);
		userService.register(user);

		verify(userRepository, times(1)).save(Mockito.any(User.class));
	}

}
