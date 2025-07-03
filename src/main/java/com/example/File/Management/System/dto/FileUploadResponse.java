package com.example.File.Management.System.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class FileUploadResponse {
    private Long id;
    private String fileName;
}
