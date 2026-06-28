package com.shivani.scribeapi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shivani.scribeapi.config.AppConstants;
import com.shivani.scribeapi.dto.ApiResponse;
import com.shivani.scribeapi.dto.PostDto;
import com.shivani.scribeapi.dto.PostResponse;
import com.shivani.scribeapi.service.FileService;
import com.shivani.scribeapi.service.PostService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final FileService fileService;

	@Value("${project.image}")
	private String path;

	@PostMapping("/user/{userId}/category/{categoryId}")
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto,
			@PathVariable("userId") Integer userId, @PathVariable("categoryId") Integer categoryId) 
	{
		PostDto createPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<>(createPost, HttpStatus.CREATED);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {
		List<PostDto> posts = this.postService.getPostsByUser(userId);
		return ResponseEntity.ok(posts);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId) {
		List<PostDto> posts = this.postService.getPostsByCategory(categoryId);
		return ResponseEntity.ok(posts);
	}

	@GetMapping
	public ResponseEntity<PostResponse> getAllPost(
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
		return ResponseEntity.ok(postResponse);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {
		PostDto postDto = this.postService.getPostById(postId);
		return ResponseEntity.ok(postDto);
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<ApiResponse> deletePost(@PathVariable Integer postId) {
		this.postService.deletePost(postId);
		return ResponseEntity.ok(new ApiResponse("Post is successfully deleted !!", true));
	}

	@PutMapping("/{postId}")
	public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto, @PathVariable Integer postId) {
		PostDto updatePost = this.postService.updatePost(postDto, postId);
		return ResponseEntity.ok(updatePost);
	}

	@GetMapping("/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable String keywords) {
		List<PostDto> result = this.postService.searchPosts(keywords);
		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/image/upload/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // FIXED HERE
	public ResponseEntity<PostDto> uploadPostImage(
	        @RequestParam("image") MultipartFile image, 
	        @PathVariable Integer postId) throws IOException {
	    
	    PostDto postDto = this.postService.getPostById(postId);
	    
	    if (postDto.getImageName() != null && !postDto.getImageName().equals("default.png")) {
	        Path oldFilePath = Paths.get(path).resolve(postDto.getImageName());
	        Files.deleteIfExists(oldFilePath);
	    }
	    
	    String fileName = this.fileService.uploadImage(path, image);
	    postDto.setImageName(fileName);
	    
	    PostDto updatePost = this.postService.updatePost(postDto, postId);
	    return ResponseEntity.ok(updatePost);
	}

	//adaptive image processing with internal classpath fallbacks
	@GetMapping(value = "/image/{imageName}")
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resourceStream;
		String contentType;

		Path externalFilePath = Paths.get(path).resolve(imageName);

		// Fallback check: If the system demands default template or external files are missing
		if ("default.png".equals(imageName) || !Files.exists(externalFilePath)) {
			Resource classPathResource = new ClassPathResource("static/images/default.png");
			resourceStream = classPathResource.getInputStream();
			contentType = MediaType.IMAGE_PNG_VALUE;
		} else {
			resourceStream = this.fileService.getResource(path, imageName);
			contentType = URLConnection.guessContentTypeFromName(imageName);
			if (contentType == null) {
				contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}
		}

		response.setContentType(contentType);
		try (InputStream is = resourceStream) {
			StreamUtils.copy(is, response.getOutputStream());
		}
	}
}
