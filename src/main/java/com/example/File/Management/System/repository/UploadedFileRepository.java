

package com.example.File.Management.System.repository;

import com.example.File.Management.System.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    Optional<UploadedFile> findByFileName(String fileName);
}
