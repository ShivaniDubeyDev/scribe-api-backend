package com.shivani.scribeapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtAuthRequest {

	@NotBlank(message = "Authentication user identifier email string cannot be blank !!")
	@Email(message = "The username metric must align with a valid structural email layout !!")
	private String username;
	
	@NotBlank(message = "Authentication password string cannot be empty !!")
	private String password;
}
