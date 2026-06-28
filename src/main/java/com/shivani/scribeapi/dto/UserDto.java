package com.shivani.scribeapi.dto;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty; 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserDto {

	private int id;

	@NotEmpty(message = "Username cannot be empty !!")
	@Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters !!")
	private String name;

	@Email(message = "Provided email address layout is invalid !!")
	@NotEmpty(message = "Email parameter field is strictly required !!")
	private String email;

	@NotEmpty(message = "Password field is strictly required !!")
	@Size(min = 6, max = 255, message = "Password must be at least 6 characters long !!")

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@NotEmpty(message = "About metric details section cannot be blank !!")
	private String about;
	
	private Set<RoleDto> roles = new HashSet<>();
	
}
