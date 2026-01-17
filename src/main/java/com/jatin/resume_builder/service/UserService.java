package com.jatin.resume_builder.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jatin.resume_builder.document.User;
import com.jatin.resume_builder.dto.AuthResponse;
import com.jatin.resume_builder.dto.LoginRequest;
import com.jatin.resume_builder.dto.RegisterRequest;
import com.jatin.resume_builder.exceptions.ResourceExistsException;
import com.jatin.resume_builder.repository.UserRepository;
import com.jatin.resume_builder.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    private User toDocument(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscritptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(Instant.now().plusSeconds(24 * 60 * 60))
                .build();
    }

    private AuthResponse toResponse(User newUser) {
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .subscriptionPlan(newUser.getSubscritptionPlan())
                .emailVerified(newUser.isEmailVerified())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private void sendVerificationEmail(User newUser) {
        log.info("Inside User Service - sendVerificationEmail() : {}", newUser);
        try {
            String link = appBaseUrl + "/api/auth/verify-email?token=" + newUser.getVerificationToken();
            String html =
                    "<div style='font-family:sans-serif'>" +
                            "<h2>Verify your email</h2>" +
                            "<p>Hi " + newUser.getName() + ", please confirm your email to activate your account.</p>" +
                            "<p><a href=\"" + link + "\" " +
                            "style='display:inline-block;padding:10px 16px;background:#6366f1;color:#fff;text-decoration:none;border-radius:6px'>" +
                            "Verify Email</a></p>" +
                            "<p>Or copy this link: <br>" + link + "</p>" +
                            "<p>This link expires in 24 hours.</p>" +
                            "</div>";

            emailService.sendHtmlEmail(newUser.getEmail(), "Verify your email", html);
        }
        catch(Exception e) {
            log.error("Error occured at sendVerificationEmail() : {}", e.getMessage());
            throw new RuntimeException("Failed to send verification email");
        }
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService : register() {} ", request);

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceExistsException("User already exists with this email");
        }

        User newUser = toDocument(request);

        userRepository.save(newUser);

        sendVerificationEmail(newUser);

        return toResponse(newUser);
    }

    public void verifyEmail(String token) {
        log.info("Inside user service verifyEmail() : {}", token);

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token"));

        if(user.getVerificationExpires() != null &&
                user.getVerificationExpires().isBefore(Instant.now())) {
            throw new RuntimeException("Verfication token expired request new one");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        if(!existingUser.isEmailVerified()) {
            throw new RuntimeException("Verify email before logging-in");
        }

        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = toResponse(existingUser);
        response.setToken(token);

        return response;
    }

    public void resendVerificationMail(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(Instant.now().plusSeconds(24 * 60 * 60));

        userRepository.save(user);

        sendVerificationEmail(user);
    }

    public AuthResponse getProfile(Object principalObject) {
        User existingUser = (User)principalObject;
        return toResponse(existingUser);
    }
    
    public void changePlan(Object principalObject) {
    	AuthResponse response = getProfile(principalObject);
    	
    	User existingUser = userRepository.findById(response.getId())
    	        .orElseThrow(() -> new RuntimeException("User not found"));
    	
    	existingUser.setSubscritptionPlan("premium");
        userRepository.save(existingUser);
    }
}
