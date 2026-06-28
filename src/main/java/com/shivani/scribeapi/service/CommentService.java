package com.shivani.scribeapi.service;

import com.shivani.scribeapi.dto.CommentDto;

public interface CommentService {

	// UPDATED: Added String userEmail as the third parameter
	CommentDto createComment(CommentDto commentDto, Integer postId, String userEmail);

	void deleteComment(Integer commentId);
}
