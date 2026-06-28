package com.shivani.scribeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivani.scribeapi.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

}
