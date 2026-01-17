package com.jatin.resume_builder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

	@NotBlank(message="Name can not be blank")
	@Size(min=2, max=15, message="Name must be between 2 and 15 characters")
	private String name;
	
	@Email(message="Email structure not correct")
	@NotBlank(message="Email can not be blank")
	private String email;
	
	@NotBlank(message="Password is required")
	@Size(min=8, max=15, message="Password must be between 8 and 15 characters")
	private String password;
	
	private String profileImageUrl;
}
