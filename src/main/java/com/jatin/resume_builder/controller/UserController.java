package com.jatin.resume_builder.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jatin.resume_builder.document.User;
import com.jatin.resume_builder.dto.AuthResponse;
import com.jatin.resume_builder.dto.LoginRequest;
import com.jatin.resume_builder.dto.RegisterRequest;
import com.jatin.resume_builder.service.FileUploadService;
import com.jatin.resume_builder.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
@Tag(name="User Controller", description = "Handle User Authentication login, singup")
public class UserController {

	private final UserService userService;
	private final FileUploadService fileUploadService;
	
	@PostMapping("/register")
	@Operation(summary = "Register User")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
		
		AuthResponse resposne = userService.register(request);
		log.info("Reponse from service : {}", resposne);
		return ResponseEntity.status(HttpStatus.CREATED).body(resposne);
	}
	
	@GetMapping("/verify-email")
	@Operation(summary = "Veriy Email")
	public ResponseEntity<?> verifyEmail(@RequestParam String token){
		userService.verifyEmail(token);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email verified successfully"));
	}
	
	@PostMapping("/upload-image")
	@Operation(summary = "Upload profile image")
	public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException{
		Map<String, String> response = fileUploadService.uploadSingleImage(file);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/login")
	@Operation(summary = "Login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
		AuthResponse response = userService.login(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/resend-verification")
	@Operation(summary = "Resend Verification Email ")
	public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body){
		// get email from request
		String email = body.get("email");
		
		// add validations
		if(Objects.isNull(email)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Email is Required"));
		}
		
		// call service method to resend verification mail
		userService.resendVerificationMail(email);
		
		// return response
		return ResponseEntity.ok(Map.of("success", true, "message", "Verification email sent"));
	}
	
	@GetMapping("/profile")
	@Operation(summary = "Get user profile")
	public ResponseEntity<?> getProfile(Authentication authentication){
		// get principal object
		Object principalObject = authentication.getPrincipal();
		
		// call service method
		AuthResponse currectProfile = userService.getProfile(principalObject); 
		
		// return response
		return ResponseEntity.ok(currectProfile);
	}
	
	

}
