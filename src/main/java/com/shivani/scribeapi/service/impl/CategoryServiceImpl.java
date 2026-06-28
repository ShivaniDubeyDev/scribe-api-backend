package com.shivani.scribeapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.shivani.scribeapi.dto.CategoryDto;
import com.shivani.scribeapi.dto.CategoryResponse;
import com.shivani.scribeapi.entity.Category;
import com.shivani.scribeapi.exception.ResourceNotFoundException;
import com.shivani.scribeapi.repository.CategoryRepo;
import com.shivani.scribeapi.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepo categoryRepo;
	private final ModelMapper modelMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CategoryDto createCategory(CategoryDto categoryDto) {
		Category cat = this.modelMapper.map(categoryDto, Category.class);
		Category addedCat = this.categoryRepo.save(cat);
		return this.modelMapper.map(addedCat, CategoryDto.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

		cat.setCategoryTitle(categoryDto.getCategoryTitle());
		cat.setCategoryDescription(categoryDto.getCategoryDescription());

		Category updatedCat = this.categoryRepo.save(cat);
		return this.modelMapper.map(updatedCat, CategoryDto.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCategory(Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
		this.categoryRepo.delete(cat);
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryDto getCategorybyId(Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
		return this.modelMapper.map(cat, CategoryDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
	    
	    // 1. Handle sorting cleanly
	    Sort sort = sortDir.equalsIgnoreCase("asc")
	            ? Sort.by(sortBy).ascending()
	            : Sort.by(sortBy).descending();

	    // 2. Setup pagination 
	    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
	    Page<Category> pageCategory = this.categoryRepo.findAll(pageable);

	    // 3. Convert Categories to CategoryDtos using Streams 
	    List<Category> allCategories = pageCategory.getContent();
	    List<CategoryDto> categoryDtos = allCategories.stream()
	            .map(category -> this.modelMapper.map(category, CategoryDto.class))
	            .collect(Collectors.toList());

	    // 4. Build and return the category response
	    CategoryResponse categoryResponse = new CategoryResponse();
	    categoryResponse.setContent(categoryDtos);
	    categoryResponse.setPageNumber(pageCategory.getNumber());
	    categoryResponse.setPageSize(pageCategory.getSize());
	    categoryResponse.setTotalElements(pageCategory.getTotalElements());
	    categoryResponse.setTotalPages(pageCategory.getTotalPages());
	    categoryResponse.setLastPage(pageCategory.isLast());

	    return categoryResponse;
	}
}
