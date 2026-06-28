package com.shivani.scribeapi.service;

import com.shivani.scribeapi.dto.UserDto;
import com.shivani.scribeapi.dto.UserResponse;

public interface UserService {

	UserDto registerNewUser(UserDto user);
	
	UserDto createUser(UserDto user);

	UserDto updateUser(UserDto user, Integer userId);

	UserDto getUserById(Integer userId);

	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	void deleteUser(Integer userId);

}
