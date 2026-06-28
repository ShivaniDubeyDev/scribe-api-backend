package com.shivani.scribeapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shivani.scribeapi.entity.Category;
import com.shivani.scribeapi.entity.Post;
import com.shivani.scribeapi.entity.User;

public interface PostRepo extends JpaRepository<Post, Integer> {

	// Uses JOIN FETCH to completely eliminate the N+1 query bottleneck
	@Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category WHERE p.user = :user")
	List<Post> findByUser(@Param("user") User user);

	// Fetches posts and matching categories in one single database round trip
	@Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category WHERE p.category = :category")
	List<Post> findByCategory(@Param("category") Category category);

	// Enterprise Search Optimization: Case-insensitive keyword matching
	@Query("SELECT p FROM Post p JOIN FETCH p.user JOIN FETCH p.category WHERE LOWER(p.title) LIKE LOWER(:key)")
	List<Post> searchByTitle(@Param("key") String title);
}
