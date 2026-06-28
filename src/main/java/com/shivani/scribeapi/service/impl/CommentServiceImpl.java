package com.shivani.scribeapi.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivani.scribeapi.dto.CommentDto;
import com.shivani.scribeapi.entity.Comment;
import com.shivani.scribeapi.entity.Post;
import com.shivani.scribeapi.entity.User;
import com.shivani.scribeapi.exception.ResourceNotFoundException;
import com.shivani.scribeapi.repository.CommentRepo;
import com.shivani.scribeapi.repository.PostRepo;
import com.shivani.scribeapi.repository.UserRepo; 
import com.shivani.scribeapi.service.CommentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final PostRepo postRepo;
	private final CommentRepo commentRepo;
	private final UserRepo userRepo; // Added dependency to find user by email
	private final ModelMapper modelMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CommentDto createComment(CommentDto commentDto, Integer postId, String userEmail) { 
		// 1. Fetch the target post
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));

		// 2. Fetch the secure user from token principal identifier
		User user = this.userRepo.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Email", 0));

		// 3. Map DTO to Entity and bind relations
		Comment comment = this.modelMapper.map(commentDto, Comment.class);
		comment.setPost(post);
		comment.setUser(user); // Links author user to the comment

		Comment savedComment = this.commentRepo.save(comment);
		return this.modelMapper.map(savedComment, CommentDto.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteComment(Integer commentId) {
		Comment com = this.commentRepo.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment Id", commentId));
		this.commentRepo.delete(com);
	}
}
