package com.shivani.scribeapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CommentDto {

	private int id;

	@NotBlank(message = "Comment content cannot be blank !!")
	@Size(max = 1000, message = "Comment length cannot exceed 1000 characters !!")
	private String content;

	// Allows ModelMapper to automatically bind comment authors safely
	private UserDto user;
}
