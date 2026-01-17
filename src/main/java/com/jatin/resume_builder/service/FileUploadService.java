package com.jatin.resume_builder.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jatin.resume_builder.document.Resume;
import com.jatin.resume_builder.dto.AuthResponse;
import com.jatin.resume_builder.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {
	
	private final Cloudinary cloudinary;
	private final UserService userService;
	private final ResumeRepository resumeRepository;
	
	public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException{
		Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));
		return Map.of("image_url", imageUploadResult.get("secure_url").toString());
	}

	public Map<String, String> uploadResumeImages(String resumeId, Object principal, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
		
		// get profile
		AuthResponse response = userService.getProfile(principal);
		
		// get existing resume
		Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
				  .orElseThrow(() -> new RuntimeException("Resume not found"));
		
		// upload resume images and set the resume url
		Map<String, String> returnValue = new HashMap<>();
		Map<String, String> uploadResult;
		
		if(Objects.nonNull(thumbnail)) {
			uploadResult = uploadSingleImage(thumbnail);
			returnValue.put("thumbnailLink", uploadResult.get("image_url")); // add in result map
			existingResume.setThumbnailLink(uploadResult.get("image_url")); // set in resume
		}
		
		if(Objects.nonNull(profileImage)) {
			uploadResult = uploadSingleImage(profileImage);
			if(Objects.isNull(existingResume.getProfileInfo())){
				existingResume.setProfileInfo(new Resume.ProfileInfo());
			}
			returnValue.put("profilePreviewUrl", uploadResult.get("image_url")); // add in result map
			existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("image_url")); // set in resume
		}
		
		// save details in db
		resumeRepository.save(existingResume);
		returnValue.put("message", "Images uploaded succesfully");
		
		// retrun res
		return returnValue;
	}
}
