

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

    private static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");

    public FileStorageService(UploadedFileRepository repository) {
        this.repository = repository;
    }

    public String storeFiles(MultipartFile[] files, List<FileUploadResponse> responses) throws IOException {
        List<String> duplicateFiles = new ArrayList<>();
        List<String> invalidFiles = new ArrayList<>();

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        for (MultipartFile file : files) {
            String originalName = Path.of(file.getOriginalFilename()).getFileName().toString();

            if (!VALID_FILENAME_PATTERN.matcher(originalName).matches()) {
                invalidFiles.add(originalName);
                continue;
            }else if (repository.findByFileName(originalName).isPresent()) {
                duplicateFiles.add(originalName);
                continue;
            }

            Path targetPath = dirPath.resolve(originalName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile saved = repository.save(new UploadedFile(originalName));
            responses.add(new FileUploadResponse(saved.getId(), saved.getFileName()));
        }

        StringBuilder status = new StringBuilder();
        if (!duplicateFiles.isEmpty()) {
            status.append("Duplicate files skipped: ").append(String.join(", ", duplicateFiles)).append(". ");
        }
        if (!invalidFiles.isEmpty()) {
            status.append("Invalid filenames skipped: ").append(String.join(", ", invalidFiles)).append(". ");
        }
        if (responses.isEmpty() && status.length() == 0) {
            status.append("No files uploaded.");
        } else if (!responses.isEmpty() && status.length() == 0) {
            status.append("All files uploaded successfully.");
        }

        return status.toString().trim();
    }

    public String deleteFileById(Long id) throws IOException {
        UploadedFile file = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + id));

        Path filePath = Paths.get(uploadDir).resolve(file.getFileName());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        repository.deleteById(id);

        return "File '" + file.getFileName() + "' deleted successfully.";
    }

    public List<UploadedFile> getAllFiles() {
        return repository.findAll();
    }

    public UploadedFile getFileById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + id));
    }



}
