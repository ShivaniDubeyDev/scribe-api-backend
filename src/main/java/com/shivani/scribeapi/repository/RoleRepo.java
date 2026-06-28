package com.shivani.scribeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivani.scribeapi.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {
	// Core system security roles abstraction tier
}
