package com.shivani.scribeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivani.scribeapi.entity.Comment; 

public interface CommentRepo extends JpaRepository<Comment, Integer> {
	// Scalable CRUD abstraction handled completely by JpaRepository
}
