package com.shivani.scribeapi.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivani.scribeapi.dto.PostDto;
import com.shivani.scribeapi.dto.PostResponse;
import com.shivani.scribeapi.entity.Category;
import com.shivani.scribeapi.entity.Post;
import com.shivani.scribeapi.entity.User;
import com.shivani.scribeapi.exception.ResourceNotFoundException;
import com.shivani.scribeapi.repository.CategoryRepo;
import com.shivani.scribeapi.repository.PostRepo;
import com.shivani.scribeapi.repository.UserRepo;
import com.shivani.scribeapi.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostRepo postRepo;
	private final ModelMapper modelMapper;
	private final UserRepo userRepo;
	private final CategoryRepo categoryRepo;
	
	@Value("${project.image}")
	private String imagePath;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryId));

		Post post = this.modelMapper.map(postDto, Post.class);
		post.setImageName("default.png");
		post.setAddedDate(LocalDateTime.now()); 
		post.setUser(user);
		post.setCategory(category);

		Post newPost = this.postRepo.save(post);
		return this.modelMapper.map(newPost, PostDto.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PostDto updatePost(PostDto postDto, Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post id", postId));

		if (postDto.getCategory() != null && postDto.getCategory().getCategoryId() != null) {
			Category category = this.categoryRepo.findById(postDto.getCategory().getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", postDto.getCategory().getCategoryId()));
			post.setCategory(category);
		}

		post.setTitle(postDto.getTitle());
		post.setContent(postDto.getContent());
		post.setImageName(postDto.getImageName());

		Post updatedPost = this.postRepo.save(post);
		return this.modelMapper.map(updatedPost, PostDto.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deletePost(Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post id", postId));
		
		// Prunes orphaned files safely from physical storage volumes
		if (post.getImageName() != null && !post.getImageName().equals("default.png")) {
			try {
				java.nio.file.Path fileToDelete = java.nio.file.Paths.get(imagePath).resolve(post.getImageName());
				java.nio.file.Files.deleteIfExists(fileToDelete);
			} catch (IOException e) {
				System.err.println("Could not delete physical asset file: " + e.getMessage());
			}
		}
		
		this.postRepo.delete(post);
	}

	@Override
	@Transactional(readOnly = true)
	public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Post> pagePost = this.postRepo.findAll(pageable);

		List<Post> allPosts = pagePost.getContent();
		List<PostDto> postDtos = allPosts.stream()
				.map(post -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePost.getNumber());
		postResponse.setPageSize(pagePost.getSize());
		postResponse.setTotalElements(pagePost.getTotalElements());
		postResponse.setTotalPages(pagePost.getTotalPages());
		postResponse.setLastPage(pagePost.isLast());

		return postResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public PostDto getPostById(Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post id", postId));
		return this.modelMapper.map(post, PostDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PostDto> getPostsByCategory(Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryId));
		
		List<Post> posts = this.postRepo.findByCategory(cat);
		return posts.stream()
				.map(post -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<PostDto> getPostsByUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));
		
		List<Post> posts = this.postRepo.findByUser(user);
		return posts.stream()
				.map(post -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<PostDto> searchPosts(String keyword) {
		List<Post> posts = this.postRepo.searchByTitle("%" + keyword + "%");
		return posts.stream()
				.map(post -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
	}
}
