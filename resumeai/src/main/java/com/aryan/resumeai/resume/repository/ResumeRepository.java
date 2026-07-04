package com.aryan.resumeai.resume.repository;

import com.aryan.resumeai.auth.entity.User;
import com.aryan.resumeai.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    // Find all resumes uploaded by a specific user
    List<Resume> findByUserOrderByCreatedAtDesc(User user);
}