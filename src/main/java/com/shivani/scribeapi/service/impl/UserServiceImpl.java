package com.shivani.scribeapi.service.impl;

import java.util.List;

import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shivani.scribeapi.config.AppConstants;
import com.shivani.scribeapi.dto.UserDto;
import com.shivani.scribeapi.dto.UserResponse;
import com.shivani.scribeapi.entity.Role;
import com.shivani.scribeapi.entity.User;
import com.shivani.scribeapi.exception.ResourceNotFoundException;
import com.shivani.scribeapi.repository.RoleRepo;
import com.shivani.scribeapi.repository.UserRepo;
import com.shivani.scribeapi.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Constructor Injection
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepo roleRepo;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserDto createUser(UserDto userDto) {

		User user = this.modelMapper.map(userDto, User.class);

		// Hash the password during direct creation cycles
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		User savedUser = this.userRepo.save(user);

		UserDto responseDto = this.modelMapper.map(savedUser, UserDto.class);
		responseDto.setPassword(null); // Cleans the password tracking reference
		return responseDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserDto updateUser(UserDto userDto, Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setAbout(userDto.getAbout());

		// Hash the updated password safely if provided
		if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
			user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
		}

		User updatedUser = this.userRepo.save(user);

		UserDto responseDto = this.modelMapper.map(updatedUser, UserDto.class);
		responseDto.setPassword(null); // Cleans the password tracking reference
		return responseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		UserDto responseDto = this.modelMapper.map(user, UserDto.class);
		responseDto.setPassword(null); // Cleans the password tracking reference
		return responseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

		// 1. Handle sorting cleanly
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		// 2. Setup pagination
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<User> pageUser = this.userRepo.findAll(pageable);

		// 3. Convert Users to UserDtos using Streams
		List<User> allUsers = pageUser.getContent();
		List<UserDto> userDtos = allUsers.stream().map(user -> {
			UserDto dto = this.modelMapper.map(user, UserDto.class);
			dto.setPassword(null); // Ensures list views also exclude password placeholders
			return dto;
		}).collect(Collectors.toList());

		// 4. Build and return the response
		UserResponse userResponse = new UserResponse();
		userResponse.setContent(userDtos);
		userResponse.setPageNumber(pageUser.getNumber());
		userResponse.setPageSize(pageUser.getSize());
		userResponse.setTotalElements(pageUser.getTotalElements());
		userResponse.setTotalPages(pageUser.getTotalPages());
		userResponse.setLastPage(pageUser.isLast());

		return userResponse;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		this.userRepo.delete(user);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserDto registerNewUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);

		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// Safeguard the role fetch using a clear ResourceNotFoundException strategy
		Role role = this.roleRepo.findById(AppConstants.NORMAL_USER)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.NORMAL_USER));

		user.getRoles().add(role);
		User newUser = this.userRepo.save(user);

		// Convert back to DTO
		UserDto responseDto = this.modelMapper.map(newUser, UserDto.class);

		// Explicitly clear the encrypted password string out of memory
		responseDto.setPassword(null);

		return responseDto;
	}
}
