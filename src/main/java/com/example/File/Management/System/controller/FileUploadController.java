
package com.example.File.Management.System.controller;

import com.example.File.Management.System.dto.FileUploadResponse;
import com.example.File.Management.System.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            List<FileUploadResponse> responses = service.storeFiles(files);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading files.");
        }
    }
}

