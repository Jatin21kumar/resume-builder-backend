package com.jatin.resume_builder.controller;

import java.io.IOException;
import java.net.ResponseCache;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jatin.resume_builder.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/email")
@Tag(name = "Email Controller", description = "API for sending resumes over email")
public class EmailController {

	private final EmailService emailService;
	
	@PostMapping(value = "/send-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Send Resumes over mail")
	public ResponseEntity<Map<String, Object>> sendResumeByEmail(
			@RequestPart("recipientEmail") String recipientEmail,
			@RequestPart("subject") String subject,
			@RequestPart("message") String message,
			@RequestPart("pdfFile") MultipartFile pdfFile,
			Authentication authentication
			) throws IOException, MessagingException{
		
		// validate inputs
		Map<String, Object> response = new HashMap<>();
		if(Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)) {
			response.put("success", false);
			response.put("message", "Missing required fields");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		// get pdf data
		byte[] pdfBytes = pdfFile.getBytes();
		String originalFilename = pdfFile.getOriginalFilename();
		String filename = Objects.nonNull(originalFilename) ? originalFilename: "resume.pdf";
		
		// create email content
		String emailSubject = Objects.nonNull(subject) ? subject: "Resume Application";
		String eamilbody = Objects.nonNull(message) ? message: "Please find my resume attached.\n\n Best Regards";
		
		// call service method
		emailService.sendEmailWithAttachment(recipientEmail, emailSubject, eamilbody, pdfBytes, filename);
		
		response.put("success", true);
		response.put("message", "Resume sent successfully to " + recipientEmail);
		
		return ResponseEntity.ok(response);
	}
}
