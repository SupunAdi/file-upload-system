

package com.example.File.Management.System.service;

import com.example.File.Management.System.dto.FileUploadResponse;
import com.example.File.Management.System.entity.UploadedFile;
import com.example.File.Management.System.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final UploadedFileRepository repository;

    // Accept only letters, digits, dots, underscores, and hyphens
    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");

    public FileStorageService(UploadedFileRepository repository) {
        this.repository = repository;
    }

    public List<FileUploadResponse> storeFiles(MultipartFile[] files) throws IOException {
        List<FileUploadResponse> responses = new ArrayList<>();

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        for (MultipartFile file : files) {
            String originalName = Path.of(file.getOriginalFilename()).getFileName().toString();

            // Validate
            if (!VALID_FILENAME_PATTERN.matcher(originalName).matches()) {
                throw new IllegalArgumentException("Invalid file name: " + originalName);
            }

            // Duplicate check
            if (repository.findByFileName(originalName).isPresent()) {
                throw new IllegalArgumentException("Duplicate file: " + originalName);
            }

            // Save file
            Path targetPath = dirPath.resolve(originalName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Save DB record
            UploadedFile saved = repository.save(new UploadedFile(originalName));

            responses.add(new FileUploadResponse(saved.getId(), saved.getFileName()));
        }

        return responses;
    }
}

