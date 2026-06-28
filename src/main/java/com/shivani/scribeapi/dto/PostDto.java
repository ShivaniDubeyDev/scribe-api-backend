package com.shivani.scribeapi.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {

	private Integer postId;

	@NotBlank(message = "Post title cannot be blank !!")
	@Size(min = 4, max = 100, message = "Title must be between 4 and 100 characters !!")
	private String title;

	@NotBlank(message = "Post content cannot be blank !!")
	@Size(min = 10, max = 10000, message = "Content must be at least 10 characters long !!")
	private String content;

	private String imageName;

	private LocalDateTime addedDate;

	private CategoryDto category;

	private UserDto user;

	private Set<CommentDto> comments = new HashSet<>();
}
