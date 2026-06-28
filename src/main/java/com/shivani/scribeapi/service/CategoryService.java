package com.shivani.scribeapi.service;

import com.shivani.scribeapi.dto.CategoryDto;
import com.shivani.scribeapi.dto.CategoryResponse;

public interface CategoryService {

	// create
	CategoryDto createCategory(CategoryDto categoryDto);

	// update
	CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId);

	// delete
	void deleteCategory(Integer categoryId);

	// get
	CategoryDto getCategorybyId(Integer categoryId);

	// get All
	CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

}
