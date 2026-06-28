package com.shivani.scribeapi.controller;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivani.scribeapi.dto.ApiResponse;
import com.shivani.scribeapi.dto.CommentDto;
import com.shivani.scribeapi.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/posts/{postId}/comments") 
	public ResponseEntity<CommentDto> createComment(
			@Valid @RequestBody CommentDto commentDto, 
			@PathVariable Integer postId,
			Principal principal) {
		
		// Securely routes context username to the service tier
		CommentDto createComment = this.commentService.createComment(commentDto, postId, principal.getName());
		return new ResponseEntity<>(createComment, HttpStatus.CREATED);
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId) {
		this.commentService.deleteComment(commentId);
		return ResponseEntity.ok(new ApiResponse("Comment deleted successfully !!", true));
	}
}
