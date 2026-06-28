package com.shivani.scribeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivani.scribeapi.config.AppConstants;
import com.shivani.scribeapi.dto.ApiResponse;
import com.shivani.scribeapi.dto.CategoryDto;
import com.shivani.scribeapi.dto.CategoryResponse;
import com.shivani.scribeapi.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
		CategoryDto createCategory = this.categoryService.createCategory(categoryDto);
		return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
	}

	@PutMapping("/{catId}")
	public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Integer catId) {
		CategoryDto updatedCategory = this.categoryService.updateCategory(categoryDto, catId);
		return ResponseEntity.ok(updatedCategory);
	}

	@DeleteMapping("/{catId}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer catId) {
		this.categoryService.deleteCategory(catId);
		return ResponseEntity.ok(new ApiResponse("Category is deleted successfully !!", true));
	}

	@GetMapping("/{catId}")
	public ResponseEntity<CategoryDto> getCategorybyId(@PathVariable Integer catId) {
		CategoryDto categoryDto = this.categoryService.getCategorybyId(catId);
		return ResponseEntity.ok(categoryDto);
	}

	@GetMapping
	public ResponseEntity<CategoryResponse> getAllCategories(
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy, // Change to matches field name (like catId or id)
			@RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {
		
		CategoryResponse categoryResponse = this.categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortDir);
		return ResponseEntity.ok(categoryResponse);
	}
}
