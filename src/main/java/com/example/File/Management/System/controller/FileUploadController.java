
package com.example.File.Management.System.controller;

import com.example.File.Management.System.dto.FileUploadResponse;
import com.example.File.Management.System.entity.UploadedFile;
import com.example.File.Management.System.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService service;

    public FileUploadController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> responses = new ArrayList<>();
            String statusMessage = service.storeFiles(files, responses);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("uploaded", responses);
            responseMap.put("message", statusMessage);

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error uploading files."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            String message = service.deleteFileById(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete file from disk."));
        }
    }

    @GetMapping
    public ResponseEntity<List<UploadedFile>> listAllFiles() {
        return ResponseEntity.ok(service.getAllFiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFileById(@PathVariable Long id) {
        try {
            UploadedFile file = service.getFileById(id);
            return ResponseEntity.ok(file);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }



}

