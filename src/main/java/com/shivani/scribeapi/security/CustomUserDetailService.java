package com.shivani.scribeapi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.shivani.scribeapi.exception.ResourceNotFoundException;
import com.shivani.scribeapi.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //Constructor Injection
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userRepo.findByEmail(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
	}

}
