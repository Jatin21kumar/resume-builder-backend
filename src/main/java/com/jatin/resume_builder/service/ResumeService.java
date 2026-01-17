package com.jatin.resume_builder.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jatin.resume_builder.document.Resume;
import com.jatin.resume_builder.dto.AuthResponse;
import com.jatin.resume_builder.dto.CreateResumeRequest;
import com.jatin.resume_builder.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
	private final ResumeRepository resumeRepository;
	private final UserService userService;
	
	private void setDefaultResumeData(Resume newResume) {

	    newResume.setProfileInfo(new Resume.ProfileInfo());
	    newResume.setContactInfo(new Resume.ContactInfo());

	    newResume.setWorkExperience(new ArrayList<>());
	    newResume.setEducations(new ArrayList<>());
	    newResume.setSkills(new ArrayList<>());
	    newResume.setProjects(new ArrayList<>());
	    newResume.setCertifications(new ArrayList<>());
	    newResume.setLanguages(new ArrayList<>());
	    newResume.setHobbies(new ArrayList<>());
	}

	
	public Resume createResume(CreateResumeRequest request, Object principalObject) {
	
		// create resume object
		Resume newResume = new Resume();
		
		// get current profile
		AuthResponse response = userService.getProfile(principalObject);
		
		// update resume
		newResume.setUserId(response.getId());
		newResume.setTitle(request.getTitle());
		
		// set default data for resume
		setDefaultResumeData(newResume);
		
		// save data to db
		return resumeRepository.save(newResume);
	}


	public List<Resume> getUserResumes(Object principal) {
		// get current profile
		AuthResponse response = userService.getProfile(principal);
		
		// call repository findAll method
		List<Resume> resumes = resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
		
		// return response
		return resumes;
	}


	public Resume getResumeById(String resumeId, Object principal) {
		// get profile
		AuthResponse response = userService.getProfile(principal);
		
		// call repository to get resume details
		Resume resume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
												  .orElseThrow(() -> new RuntimeException("Resume not found"));
		
		// return result
		return resume;
	}


	public Resume updateResume(String resumeId, Resume updatedResume, Object principal) {
		// get profile
		AuthResponse response = userService.getProfile(principal);
		
		Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
				  .orElseThrow(() -> new RuntimeException("Resume not found"));
		
		 	existingResume.setTitle(updatedResume.getTitle());
		    existingResume.setThumbnailLink(updatedResume.getThumbnailLink());
		    existingResume.setTemplate(updatedResume.getTemplate());
	
		    existingResume.setProfileInfo(updatedResume.getProfileInfo());
		    existingResume.setContactInfo(updatedResume.getContactInfo());
	
		    existingResume.setWorkExperience(updatedResume.getWorkExperience());
		    existingResume.setEducations(updatedResume.getEducations());
		    existingResume.setSkills(updatedResume.getSkills());
		    existingResume.setProjects(updatedResume.getProjects());
		    existingResume.setCertifications(updatedResume.getCertifications());
		    existingResume.setLanguages(updatedResume.getLanguages());
		    existingResume.setHobbies(updatedResume.getHobbies());
	    
		   resumeRepository.save(existingResume); 

		   return existingResume;
	}


	public void deleteResume(String resumeId, Object principal) {
		// get existing resume
		Resume existingResume = getResumeById(resumeId, principal);
		
		// delete from repository
		resumeRepository.delete(existingResume);
	}
	
}
