package com.jankovicd.flightadvisor.auth.service;

import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jankovicd.flightadvisor.auth.dto.UserDTO;
import com.jankovicd.flightadvisor.auth.enums.RoleEnum;
import com.jankovicd.flightadvisor.auth.model.User;
import com.jankovicd.flightadvisor.auth.repository.UserRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.util.Bundle;

@Service
public class UserService implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return user.get();
		}
		throw new FlightAdvisorException(
				MessageFormat.format(Bundle.getBundleMessage("user.username.invalid"), username));
	}

	public void register(UserDTO userDTO) {
		User user = new User();
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setUsername(userDTO.getUsername());
		user.setRole(RoleEnum.REGULAR);
		String password = encodePassword(userDTO.getPassword());
		user.setPassword(password);
		user.setSalt(password.substring(7, 29));
		userRepository.save(user);
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

}
