package com.interview.WebApp.repository;

import com.interview.WebApp.entity.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<Upload, Long> {
} 