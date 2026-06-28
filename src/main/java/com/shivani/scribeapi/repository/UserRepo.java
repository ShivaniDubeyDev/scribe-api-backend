package com.shivani.scribeapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivani.scribeapi.entity.User; 

public interface UserRepo extends JpaRepository<User, Integer> {
	
	// Fast indexing query lookup for Spring Security Authentication mechanisms
	Optional<User> findByEmail(String email);
}
