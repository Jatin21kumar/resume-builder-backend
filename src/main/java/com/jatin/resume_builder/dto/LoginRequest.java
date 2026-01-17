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
public class LoginRequest {

	@Email(message="Email structure must be correct")
	@NotBlank(message="Email can not be blank")
	private String email;
	
	@NotBlank(message="Password can nto be blank")
	private String password;
	
}
